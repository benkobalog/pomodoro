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

    val websocket = routeAuthentication.routeWithAuthentication(
      pomodoroEndpoints.webSocketRoute)

    val port = 9001

    val httpBind =
      Http().bindAndHandle(
        DebuggingDirectives.logResult("http", Logging.InfoLevel)(
          routeWithCorsAndAuth),
        "localhost",
        port)

    val wsBind = Http().bindAndHandle(
      DebuggingDirectives.logResult("ws", Logging.InfoLevel)(websocket),
      "localhost",
      port + 1)

    println(
      s"Server online at http://localhost:$port/\nPress RETURN to stop...")
    StdIn.readLine()

    httpBind
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())

    wsBind
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}
