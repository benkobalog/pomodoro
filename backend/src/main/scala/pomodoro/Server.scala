package pomodoro

import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.DebuggingDirectives
import pomodoro.endpoints._

import scala.io.StdIn

object Server extends Dependencies {
  def main(args: Array[String]): Unit = {

    val routes = (userId: java.util.UUID) => {
      pomodoroEndpoints.route(userId) ~
        authEndpoints.route ~
        userEndpoints.route(userId)
    }

    val routeWithCorsAndAuth = CORSHandler.corsHandler(
      routeAuthentication.routeWithAuthentication(routes))

    val port = 9001

    val bindingFuture =
      Http().bindAndHandle(
        DebuggingDirectives.logResult("stuff", Logging.InfoLevel)(
          routeWithCorsAndAuth),
        "localhost",
        port)

    println(
      s"Server online at http://localhost:$port/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}
