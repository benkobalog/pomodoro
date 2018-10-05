package pomodoro

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.softwaremill.macwire._
import pomodoro.endpoints._
import pomodoro.logic.{ActorEventBus, PomodoroLogic}
import pomodoro.repository.{PomodoroRepo, PomodoroStatsRepo}
import pomodoro.repository.postgres.{
  PasswordRepo,
  PomodoroPsqlRepo,
  PomodoroStatsPsqlRepo,
  UserRepo
}
import slick.jdbc.JdbcBackend

import scala.concurrent.ExecutionContextExecutor

trait Dependencies {
  // Contexts
  implicit val system: ActorSystem = ActorSystem("webserver-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit val actorEventBus: ActorEventBus = new ActorEventBus()

  // DB
  lazy val dbConnection: JdbcBackend.Database = repository.PostgresConnection.db
  lazy val pomodoroRepo: PomodoroRepo = wire[PomodoroPsqlRepo]
  lazy val pStatsRepo: PomodoroStatsRepo = wire[PomodoroStatsPsqlRepo]
  lazy val userRepo: UserRepo = wire[UserRepo]
  lazy val pwRepo: PasswordRepo = wire[PasswordRepo]

  // Middleware
  lazy val currentTime: Double = System.currentTimeMillis().toDouble
  lazy val pomodoroLogic: PomodoroLogic = wire[PomodoroLogic]
  lazy val authentication: Authentication = wire[Authentication]

  // Endpoints
  lazy val pomodoroEndpoints: PomodoroEndpoints = wire[PomodoroEndpoints]
  lazy val authEndpoints: AuthEndpoints = wire[AuthEndpoints]
  lazy val userEndpoints: UserEndpoints = wire[UserEndpoints]
  lazy val routeAuthentication: RouteAuthentication = wire[RouteAuthentication]
}
