package pomodoro

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.softwaremill.macwire._
import pomodoro.endpoints._
import pomodoro.logic.PomodoroLogic
import pomodoro.repository.postgres.{
  PasswordRepo,
  PomodoroRepo,
  PomodoroRepoTrait,
  UserRepo
}
import slick.jdbc.JdbcBackend

import scala.concurrent.ExecutionContextExecutor

trait Dependencies {
  implicit val system: ActorSystem = ActorSystem("webserver-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  lazy val dbConnection: JdbcBackend.Database = repository.PostgresConnection.db
  lazy val pomodoroRepo: PomodoroRepoTrait = wire[PomodoroRepo]
  lazy val userRepo: UserRepo = wire[UserRepo]
  lazy val pwRepo: PasswordRepo = wire[PasswordRepo]
  lazy val authentication: Authentication = wire[Authentication]
  lazy val pomodoroLogic: PomodoroLogic = wire[PomodoroLogic]

  lazy val pomodoroEndpoints: PomodoroEndpoints = wire[PomodoroEndpoints]
  lazy val authenticationEndpoints: AuthenticationEndpoints =
    wire[AuthenticationEndpoints]
  lazy val userEndpoints: UserEndpoints = wire[UserEndpoints]
  lazy val routeAuthentication: RouteAuthentication = wire[RouteAuthentication]
}
