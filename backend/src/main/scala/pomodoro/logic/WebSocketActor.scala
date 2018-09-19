package pomodoro.logic

import java.util.UUID

import akka.actor.{Actor, ActorRef, Props}
import pomodoro.model._
import io.circe.generic.auto._
import io.circe.parser.decode
import io.circe.syntax._
import pomodoro.model.wsmessage.UserRequest
import pomodoro.utils.implicits.Circe._

class WebSocketActor(userId: UUID,
                     eventBus: ActorEventBus,
                     logic: PomodoroLogic)
    extends Actor {
  import WebSocketActor._
  private var wsHandle: Option[ActorRef] = None
  private var state: PomodoroState = _

  override def preStart(): Unit = {
    eventBus.subscribe(self, userId)
    state = Idle
  }

  override def receive: Receive = {
    case c: Connection =>
      c match {
        // `actorRef` is a handle to communicate back to the WebSocket user
        case ConnectWsHandle(actorRef) =>
          println(s"New client connected with userId $userId")
          wsHandle = Some(actorRef)

        case WsHandleDropped =>
          println(
            "Downstream WebSocket has been disconnected, stopping " + userId)
          eventBus.unsubscribe(self)
      }

    case UserAction(message) =>
      println("Got a UserAction")
      decode[UserRequest](message) match {
        case Left(e) =>
          println(s"Invalid ws message: {{$message}} exception: $e")
        case Right(msg) =>
          logic
            .stateChanges(userId, msg, state)
            .foreach { newState =>
              state = newState
              eventBus.publish(EventBusMessage(userId, newState))
            }
      }

    case cr: PomodoroState =>
      println("Got a message from the eventbus: " + cr)
      replyToUser(cr)
  }

  private def replyToUser(pomodoroState: PomodoroState): Unit =
    wsHandle.foreach(_ ! pomodoroState.asJson.noSpaces)
}

object WebSocketActor {
  sealed trait Connection
  case class ConnectWsHandle(actorRef: ActorRef) extends Connection
  case object WsHandleDropped extends Connection

  case class UserAction(message: String) extends AnyVal

  def props(userId: UUID,
            eventBus: ActorEventBus,
            logic: PomodoroLogic): Props =
    Props(new WebSocketActor(userId, eventBus, logic))
}
