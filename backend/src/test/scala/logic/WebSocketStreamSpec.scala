package logic

import java.util.UUID

import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.scaladsl.{Flow, Keep}
import akka.stream.testkit.scaladsl.{TestSink, TestSource}
import akka.stream.testkit.{TestPublisher, TestSubscriber}
import org.scalatest._
import repository.postgres.PomodoroRepoTrait

class WebSocketStreamSpec extends FunSuite with Matchers with ActorResources {
  implicit val pR: PomodoroRepoTrait = new PomodoroRepoStub()
  implicit def stringToMessage(s: String): Message = TextMessage.Strict(s)

  test("one stream") {
    val pomodoroLogic = new PomodoroLogic()
    val flow = pomodoroLogic.webSocket(UUID.randomUUID())

    val (pub, sub) = probeFlow(flow)

    sub.request(1)
    pub.sendNext("asdasd")
    sub.expectNext("asdasd")
  }

  test("Two streams with the same userId") {
    val pomodoroLogic = new PomodoroLogic()

    val userId = UUID.randomUUID()

    val flow1 = pomodoroLogic.webSocket(userId)
    val flow2 = pomodoroLogic.webSocket(userId)

    val (pub1, sub1) = probeFlow(flow1)
    val (pub2, sub2) = probeFlow(flow2)

    sub1.request(1)
    sub2.request(1)

    pub1.sendNext("asd")
    sub1.expectNext("asd")
    sub2.expectNext("asd")

    sub1.request(1)
    sub2.request(1)

    pub2.sendNext("dsa")
    sub1.expectNext("dsa")
    sub2.expectNext("dsa")
  }

  private def probeFlow[A, B, C](flow: Flow[A, B, C])
    : (TestPublisher.Probe[A], TestSubscriber.Probe[B]) = {
    TestSource
      .probe[A]
      .via(flow)
      .toMat(TestSink.probe[B])(Keep.both)
      .run()
  }
}
