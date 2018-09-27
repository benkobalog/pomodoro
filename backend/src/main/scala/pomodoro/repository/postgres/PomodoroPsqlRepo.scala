package pomodoro.repository
package postgres

import java.util.UUID

import pomodoro.model._
import slick.jdbc.JdbcBackend.DatabaseDef
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class PomodoroPsqlRepo(db: DatabaseDef)(implicit ec: ExecutionContext)
    extends PomodoroRepo {
  import dao.Tables.RunningPomodoros
  import dao.Tables.Pomodoros

  override def start(usersId: UUID,
                     kind: String,
                     started: Double): Future[Int] = {
    val insertQ = RunningPomodoros.map(x => (x.usersId, x.kind, x.started)) += (usersId, kind, started)
    db.run(insertQ)
  }

  override def finish(userId: UUID): Future[Int] = {
    db.run {
        val query = RunningPomodoros.filter(_.usersId === userId)
        for {
          pomodoro <- query.result.headOption
          _ <- query.delete
        } yield pomodoro
      }
      .flatMap {
        case None => Future.failed(new Exception("No Running pomodoro"))
        case Some(runningPom) =>
          db.run(
            Pomodoros +=
              runningPom.toPomodoro(System.currentTimeMillis().toDouble))
      }
  }

  override def getState(userId: UUID): Future[PomodoroState] = {
    db.run(
      RunningPomodoros.filter(_.usersId === userId).result.headOption.map {
        case Some(pomodoro) =>
          if (pomodoro.kind == "pomodoro") Running(pomodoro.started)
          else Break(pomodoro.kind, pomodoro.started)
        case None =>
          Idle
      }
    )
  }
}
