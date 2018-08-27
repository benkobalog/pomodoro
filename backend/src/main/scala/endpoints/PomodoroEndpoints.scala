package endpoints

import java.util.UUID

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import io.circe.generic.auto._
import io.circe.syntax._
import repository.postgres.PomodoroPqslRepo
import utils.implicits.AkkaHttpMarshaller._
import utils.implicits.Circe._

class PomodoroEndpoints(implicit pomodoroRepo: PomodoroPqslRepo) {

  def route(userId: UUID): Route =
    path("pomodoroStart") {
      post {
        onComplete(pomodoroRepo.start(userId))(respond(_ =>
          "Pomodoro successfully started"))
      }
    } ~
      path("pomodoroFinish") {
        patch {
          onComplete(pomodoroRepo.finish(userId))(respond(_ =>
            "Pomodoro successfully closed"))
        }
      } ~
      path("pomodoro") {
        get {
          onComplete(pomodoroRepo.getStats(userId))(respond(_.asJson))
        }
      } ~
      path("pomodoroState") {
        get {
          onComplete(pomodoroRepo.getState(userId))(respond(_.asJson))
        }
      }
}

object PomodoroEndpoints {
  def apply(userId: UUID)(implicit pomodoroRepo: PomodoroPqslRepo): Route =
    new PomodoroEndpoints().route(userId)
}
