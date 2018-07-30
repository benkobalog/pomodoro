import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.directives.Credentials
import akka.stream.ActorMaterializer
import repository.postgres.PomodoroPqslRepo

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

import endpoints.{CORSHandler, Authentication, PomodoroEndpoints}

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

    val routeWithCorsAndAuth =
      new PomodoroEndpoints().route |>
        Authentication.routeWithAuthenitcation |>
        CORSHandler.corsHandler

    val port = 9001

    val bindingFuture =
      Http().bindAndHandle(routeWithCorsAndAuth, "localhost", 9001)

    println(
      s"Server online at http://localhost:$port/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}
