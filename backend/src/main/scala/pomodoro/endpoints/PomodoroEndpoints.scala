package pomodoro.endpoints

import java.util.UUID

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{Flow, Sink, Source}
import io.circe.generic.auto._
import io.circe.syntax._
import pomodoro.logic.WebSocketActor.{
  ConnectWsHandle,
  UserAction,
  WsHandleDropped
}
import pomodoro.logic.{ActorEventBus, PomodoroLogic, WebSocketActor}
import pomodoro.repository.PomodoroStatsRepo
import pomodoro.repository.postgres.PomodoroStatsPsqlRepo
import pomodoro.utils.implicits.AkkaHttpMarshaller._
import pomodoro.utils.implicits.Circe._

import scala.concurrent.duration._

class PomodoroEndpoints(pomodoroLogic: PomodoroLogic,
                        pStatsRepo: PomodoroStatsRepo,
                        logic: PomodoroLogic)(
    implicit actorEventBus: ActorEventBus,
    system: ActorSystem) {

  def route(userId: UUID): Route =
    path("pomodoroStats") {
      get {
        onComplete(pStatsRepo.getStats(userId))(respond(_.asJson))
      }
    }

  def webSocketRoute(userId: UUID): Route =
    path("pomodoro") {
      get {
        handleWebSocketMessages(webSocketHandler(userId))
      }
    }

  def webSocketHandler(userId: UUID): Flow[Message, Message, NotUsed] = {
    val wsActor =
      system.actorOf(WebSocketActor.props(userId, actorEventBus, logic))

    val sink: Sink[Message, NotUsed] =
      Flow[Message]
        .collect { case TextMessage.Strict(str) => UserAction(str) }
        .to(Sink.actorRef(wsActor, WsHandleDropped)) // connect to the wsUser Actor

    val source: Source[Message, NotUsed] =
      Source
        .actorRef(bufferSize = 10,
                  overflowStrategy = OverflowStrategy.dropBuffer)
        .map { c: String =>
          TextMessage.Strict(c)
        }
        .mapMaterializedValue { wsHandle =>
          // the wsHandle is the way to talk back to the user, our wsUser actor needs to know about this to send
          // messages to the WebSocket user
          wsActor ! ConnectWsHandle(wsHandle)
          // don't expose the wsHandle anymore
          NotUsed
        }
        .keepAlive(maxIdle = 10.seconds,
                   () =>
                     TextMessage.Strict(
                       "Keep-alive message sent to WebSocket recipient"))

    Flow.fromSinkAndSource(sink, source)
  }
}
