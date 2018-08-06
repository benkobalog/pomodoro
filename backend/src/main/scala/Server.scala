import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.directives.Credentials
import akka.stream.ActorMaterializer
import repository.postgres.{PomodoroPqslRepo, UserPsqlRepo}

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn
import endpoints.{Authentication, CORSHandler, PomodoroEndpoints}
import akka.http.scaladsl.server.directives.DebuggingDirectives


object Server {
  implicit class PipeOps[A](val a: A) extends AnyVal {
    def |>[B](fn: A => B): B = fn(a)
  }

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("webserver-system")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    import repository.PostgresConnection.db
    implicit val pomodoroRepo: PomodoroPqslRepo = new PomodoroPqslRepo
    implicit val userRepo: UserPsqlRepo = new UserPsqlRepo

    val routeWithCorsAndAuth = CORSHandler.corsHandler(
      new Authentication().routeWithAuthentication(new PomodoroEndpoints().route))

    val port = 9001

    val bindingFuture =
      Http().bindAndHandle(DebuggingDirectives.logResult("stuff", Logging.InfoLevel)(routeWithCorsAndAuth), "localhost", 9001)

    println(
      s"Server online at http://localhost:$port/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}
