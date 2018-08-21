package repository
package postgres

import java.util.UUID

import model.{PomodoroStats, PomodoroState, Running, Idle}
import slick.jdbc.JdbcBackend.DatabaseDef
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class PomodoroPqslRepo(implicit db: DatabaseDef, ec: ExecutionContext) {
  import dao.Tables.Pomodoros

  def start(usersId: UUID) = {
    val insertQ = Pomodoros.map(_.usersId) += Some(usersId)
    db.run(insertQ)
  }

  private val current_timestamp =
    SimpleLiteral[java.sql.Timestamp]("CURRENT_TIMESTAMP")

  def finish(usersId: UUID) = {
    db.run(for {
      timeStamp <- current_timestamp.result
      nrUpdated <- Pomodoros
        .filter(_.usersId === usersId)
        .filter(_.finished.isEmpty)
        .map(_.finished)
        .update(Some(timeStamp))
    } yield nrUpdated)
  }

  def get(usersId: UUID) = {
    db.run(
      Pomodoros
        .filter(_.usersId === usersId)
        .filter(_.finished.nonEmpty)
        .sortBy(_.finished.desc)
        .take(5)
        .map(row => (row.started, row.finished.get))
        .result
        .map(_.map(x => PomodoroStats.fromInterval(x._1, x._2)))
    )
  }

  def getState(usersId: UUID): Future[PomodoroState] = {
    db.run(
      for {
        ts <- current_timestamp.result
        result <- Pomodoros
          .filter(_.usersId === usersId)
          .filter(_.finished.isEmpty)
          .map(r => (r.started, ts))
          .result
          .headOption
          .map {
            case None => Idle
            case Some((started, current)) =>
              Running((current.getTime - started.getTime) / 1000)
          }
      } yield result
    )
  }
}
