import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.directives.DebuggingDirectives
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import endpoints._
import repository.postgres.{PasswordRepo, PomodoroPqslRepo, UserPsqlRepo}

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object Server {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("webserver-system")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    import repository.PostgresConnection.db
    implicit val pomodoroRepo: PomodoroPqslRepo = new PomodoroPqslRepo
    implicit val userRepo: UserPsqlRepo = new UserPsqlRepo
    implicit val pwRepo: PasswordRepo = new PasswordRepo
    implicit val authentication: Authentication = new Authentication

    val routes = (userId: java.util.UUID) => {
      PomodoroEndpoints(userId) ~
        AuthenticationEndpoints() ~
        UserEndpoints(userId)
    }

    val routeWithCorsAndAuth = CORSHandler.corsHandler(
      new RouteAuthentication()
        .routeWithAuthentication(routes))

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
