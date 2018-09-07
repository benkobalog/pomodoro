package pomodoro.endpoints

import java.util.UUID

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import io.circe.generic.auto._
import io.circe.syntax._
import pomodoro.logic.PomodoroLogic
import pomodoro.repository.postgres.PomodoroRepo
import pomodoro.utils.implicits.AkkaHttpMarshaller._
import pomodoro.utils.implicits.Circe._

class PomodoroEndpoints(implicit pomodoroLogic: PomodoroLogic) {

  def route(userId: UUID): Route =
    path("pomodoroStart") {
      post {
        onComplete(pomodoroLogic.start(userId))(respond(_ =>
          "Pomodoro successfully started"))
      }
    } ~
      path("pomodoroFinish") {
        patch {
          onComplete(pomodoroLogic.finish(userId))(respond(_ =>
            "Pomodoro successfully closed"))
        }
      } ~
      path("pomodoro") {
        get {
          handleWebSocketMessages(pomodoroLogic.webSocket(userId))
        }
      } ~
      path("pomodoroStats") {
        get {
          onComplete(pomodoroLogic.list(userId))(respond(_.asJson))
        }
      } ~
      path("pomodoroState") {
        get {
          onComplete(pomodoroLogic.getState(userId))(respond(_.asJson))
        }
      }
}

object PomodoroEndpoints {
  def apply(userId: UUID)(implicit pomodoroLogic: PomodoroLogic): Route =
    new PomodoroEndpoints().route(userId)
}
