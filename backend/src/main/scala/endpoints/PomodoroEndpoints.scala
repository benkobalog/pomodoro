package endpoints

import java.util.UUID

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes.InternalServerError
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directive, Route, StandardRoute}
import io.circe.generic.auto._
import io.circe.syntax._
import repository.postgres.PomodoroPqslRepo
import utils.implicits.AkkaHttpMarshaller._
import utils.implicits.Circe._

import scala.util.{Failure, Success, Try}

class PomodoroEndpoints(implicit pomodoroRepo: PomodoroPqslRepo) {

  def route(uuid: UUID): Route =
    path("pomodoroStart") {
      post {
        onComplete(pomodoroRepo.start(uuid))(respond(_ =>
          "Pomodoro successfully started"))
      }
    } ~
      path("pomodoroFinish") {
        patch {
          onComplete(pomodoroRepo.finish(uuid))(respond(_ =>
            "Pomodoro successfully closed"))
        }
      } ~
      path("pomodoro") {
        get {
          onComplete(pomodoroRepo.get(uuid))(respond(_.asJson))
        }
      } ~
      path("pomodoroState") {
        get {
          onComplete(pomodoroRepo.getState(uuid))(respond(_.asJson))
        }
      }

  private def respond[A](fn: A => ToResponseMarshallable)(
      body: Try[A]): StandardRoute = body match {
    case Success(value) => complete(fn(value))
    case Failure(err) =>
      println("Error happened: " + err.toString)
      complete(InternalServerError, "Internal Server Error")
  }
}
