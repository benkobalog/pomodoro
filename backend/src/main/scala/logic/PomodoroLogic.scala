package logic

import java.util.UUID

import akka.NotUsed
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import model.{PomodoroState, PomodoroStats}
import repository.postgres.{PomodoroRepo, PomodoroRepoTrait}

import scala.concurrent.Future
import scala.concurrent.duration._

class PomodoroLogic(implicit pomodoroRepo: PomodoroRepoTrait,
                    system: ActorSystem,
                    actorMaterializer: ActorMaterializer) {

  private val pomodoroEventBus = new PomodoroEventBus

  def list(userId: UUID): Future[Seq[PomodoroStats]] =
    pomodoroRepo.getStats(userId)

  def getState(userId: UUID): Future[PomodoroState] =
    pomodoroRepo.getState(userId)

  def start(userId: UUID): Future[Int] = pomodoroRepo.start(userId)

  def finish(userId: UUID): Future[Int] = pomodoroRepo.finish(userId)

  def webSocket(userId: UUID): Flow[Message, Message, NotUsed] = {
    import WebSocketActor._
    val wsActor = system.actorOf(WebSocketActor.props(userId, pomodoroEventBus))

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

  val eventBus = new PomodoroEventBus()
}
