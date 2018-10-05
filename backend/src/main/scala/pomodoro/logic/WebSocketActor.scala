package pomodoro.logic

import java.util.UUID

import akka.actor.{Actor, ActorRef, Props}
import pomodoro.model._
import io.circe.generic.auto._
import io.circe.parser.decode
import io.circe.syntax._
import pomodoro.model.wsmessage.{ClockSync, ControlMessage, State, UserRequest}
import pomodoro.utils.implicits.Circe._

import scala.util.{Failure, Success}

class WebSocketActor(userId: UUID,
                     eventBus: ActorEventBus,
                     logic: PomodoroLogic)
    extends Actor {
  import WebSocketActor._
  private var wsHandle: Option[ActorRef] = None
  private var state: PomodoroState = _

  import context.dispatcher

  override def preStart(): Unit = {
    eventBus.subscribe(self, userId)
    logic.getState(userId).onComplete {
      case Success(dbState) => state = dbState
      case Failure(e) =>
        state = Idle
        println(e)
    }
  }

  override def receive: Receive = {
    case c: Connection =>
      handleConnection(c)

    case UserAction(message) =>
      handleUserAction(message)

    case s: State =>
      handleEventBusMessage(s)
  }

  private def handleEventBusMessage(s: State): Unit = {
    println("Got a message from the eventbus: " + s)
    if (s.pomodoroState != state) state = s.pomodoroState
    replyToUser(s)
  }

  private def handleUserAction(message: String): Unit = {
    println("Got a UserAction")
    decode[UserRequest](message) match {
      case Left(e) =>
        println(s"Invalid ws message: {{$message}} exception: $e")
      case Right(msg) =>
        logic
          .stateChanges(userId, msg, state) match {
          case BroadCast(newState) =>
            eventBus.publish(EventBusMessage(userId, State(newState)))

          case Self(newState) =>
            state = newState
            replyToUser(State(newState))
            replyToUser(ClockSync(System.currentTimeMillis().toDouble))

          case NoMessage =>
        }
    }
  }

  private def handleConnection(c: Connection): Unit = {
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
  }

  private def replyToUser(cm: ControlMessage): Unit = {
    val json = cm.asJson.noSpaces
    wsHandle.foreach(_ ! json)
  }
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
