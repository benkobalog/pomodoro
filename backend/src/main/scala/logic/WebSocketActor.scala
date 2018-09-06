package logic

import java.util.UUID

import akka.actor.{Actor, ActorRef, Props}
import akka.http.scaladsl.model.ws.TextMessage

class WebSocketActor(userId: UUID, pomodoroEventBus: PomodoroEventBus)
    extends Actor {
  import WebSocketActor._
  private var wsHandle: Option[ActorRef] = None

  override def preStart(): Unit = {
    pomodoroEventBus.subscribe(self, userId)
  }

  override def receive: Receive = {
    case c: Command =>
      c match {
        // `actorRef` is a handle to communicate back to the WebSocket user
        case ConnectWsHandle(actorRef) =>
          println("Stream connected to WebSocketActor")
          wsHandle = Some(actorRef)

        case WsHandleDropped =>
          println(
            "Downstream WebSocket has been disconnected, stopping " + userId)
          pomodoroEventBus.unsubscribe(self)

        case UserAction(message) =>
          println("Got a UserAction")
          pomodoroEventBus.publish(EventBusMessage(userId, message))
      }

    case x: String =>
      println("Got a message from the eventbus ")
      println(x)
      wsHandle.foreach(_ ! x)
  }
}

object WebSocketActor {
  sealed trait Command
  case class ConnectWsHandle(actorRef: ActorRef) extends Command
  case object WsHandleDropped extends Command
  case class UserAction(message: String) extends Command

  def props(userId: UUID, pomodoroEventBus: PomodoroEventBus): Props =
    Props(new WebSocketActor(userId, pomodoroEventBus))
}
