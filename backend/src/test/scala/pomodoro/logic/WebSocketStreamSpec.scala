package pomodoro.logic

import java.util.UUID

import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.scaladsl.{Flow, Keep}
import akka.stream.testkit.scaladsl.{TestSink, TestSource}
import akka.stream.testkit.{TestPublisher, TestSubscriber}
import io.circe.generic.auto._
import io.circe.syntax._
import org.scalatest._
import pomodoro.model.{Idle, PomodoroState}
import pomodoro.model.wsmessage._
import pomodoro.utils.implicits.Circe._

class WebSocketStreamSpec
    extends FunSuite
    with Matchers
    with WebSocketTestDeps {
  test("One stream") {
    val flow = pomodoroEndpoints.webSocketHandler(UUID.randomUUID())

    val (pub, sub) = probeFlow(flow)

    sub.request(1)
    pub.sendNext(StartPomodoro)
    sub.expectNext(Idle)

    pub.sendComplete()
  }

  test("Two streams with the same userId") {
    val userId = UUID.randomUUID()

    val flow1 = pomodoroEndpoints.webSocketHandler(userId)
    val flow2 = pomodoroEndpoints.webSocketHandler(userId)

    val (pub1, sub1) = probeFlow(flow1)
    val (pub2, sub2) = probeFlow(flow2)

    sub1.request(1)
    sub2.request(1)

    pub1.sendNext(StartPomodoro)
    sub1.expectNext(Idle)
    sub2.expectNext(Idle)

    sub1.request(1)
    sub2.request(1)

    pub2.sendNext(StartBreak("long break"))
    sub1.expectNext(Idle)
    sub2.expectNext(Idle)

    pub1.sendComplete()
    pub2.sendComplete()
  }

  private def probeFlow[A, B, C](flow: Flow[A, B, C])
    : (TestPublisher.Probe[A], TestSubscriber.Probe[B]) = {
    TestSource
      .probe[A]
      .via(flow)
      .toMat(TestSink.probe[B])(Keep.both)
      .run()
  }

  private implicit def urToMessage(ur: UserRequest): Message =
    TextMessage.Strict(ur.asJson.noSpaces)

  private implicit def urToMessage(ps: PomodoroState): Message =
    TextMessage.Strict(ps.asJson.noSpaces)
}
