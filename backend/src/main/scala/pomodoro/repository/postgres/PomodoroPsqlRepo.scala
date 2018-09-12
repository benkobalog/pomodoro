package pomodoro.repository
package postgres

import java.util.UUID

import pomodoro.model._
import slick.jdbc.JdbcBackend.DatabaseDef
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class PomodoroPsqlRepo(db: DatabaseDef)(implicit ec: ExecutionContext)
    extends PomodoroRepo {
  import dao.Tables.Pomodoros

  def start(usersId: UUID): Future[Int] = {
    val insertQ = Pomodoros.map(_.usersId) += Some(usersId)
    db.run(insertQ)
  }

  private val current_timestamp =
    SimpleLiteral[java.sql.Timestamp]("CURRENT_TIMESTAMP")

  def finish(usersId: UUID,
             currentMillis: Long = System.currentTimeMillis()): Future[Int] = {
    db.run(for {
      nrUpdated <- Pomodoros
        .filter(_.usersId === usersId)
        .filter(_.finished.isEmpty)
        .map(_.finished)
        .update(Some(currentMillis))
    } yield nrUpdated)
  }

  def getState(usersId: UUID): Future[PomodoroState] = {
    db.run(
      for {
        ts <- current_timestamp.result
        result <- Pomodoros
          .filter(_.usersId === usersId)
          .filter(_.finished.isEmpty)
          .map(r => (r.started, ts, r.kind))
          .result
          .headOption
          .map {
            case None => Idle
            case Some((started, current, "pomodoro")) =>
              Running(started)
            case Some((started, current, kind)) =>
              Break(kind, started)
          }
      } yield result
    )
  }
}
