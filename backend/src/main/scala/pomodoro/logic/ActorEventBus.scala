package pomodoro.logic

import java.util.UUID

import akka.actor.ActorRef
import akka.event.{EventBus, LookupClassification}
import pomodoro.model.wsmessage.State

final case class EventBusMessage(userId: UUID, payload: State)

class ActorEventBus extends EventBus with LookupClassification {
  override type Event = EventBusMessage
  override type Classifier = UUID
  override type Subscriber = ActorRef

  override protected def mapSize(): Int = 2

  override protected def compareSubscribers(a: Subscriber, b: Subscriber): Int =
    if (a.hashCode() > b.hashCode()) -1
    else if (a.hashCode() < b.hashCode()) 1
    else 0

  override protected def classify(event: Event): Classifier = event.userId

  override protected def publish(event: Event, subscriber: Subscriber): Unit = {
    println(s"publish: $event to $subscriber")
    subscriber ! event.payload
  }
}
