import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.directives.Credentials
import akka.stream.ActorMaterializer
import repository.postgres.PomodoroPqslRepo

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object Server {
  private def userPassAuthenticator(credentials: Credentials): Option[String] =
    credentials match {
      case p @ Credentials.Provided(id) if p.verify("1234") => Some(id)
      case _                                                => None
    }

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("webserver-system")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    import repository.PostgresConnection.db
    implicit val pomodoroRepo: PomodoroPqslRepo = new PomodoroPqslRepo

    val route = new endpoints.PomodoroEndpoints().route

    val port = 9001

    val bindingFuture =
      Http().bindAndHandle(route, "localhost", 9001)

    println(s"Server online at http://localhost:$port/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
