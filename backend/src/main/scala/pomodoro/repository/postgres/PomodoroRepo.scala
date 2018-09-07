package pomodoro.repository
package postgres

import java.util.UUID

import pomodoro.model.{PomodoroStats, PomodoroState, Running, Idle, Break, LongBreak}
import slick.jdbc.JdbcBackend.DatabaseDef
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class PomodoroRepo(implicit db: DatabaseDef, ec: ExecutionContext) extends PomodoroRepoTrait{
  import dao.Tables.Pomodoros

  def start(usersId: UUID): Future[Int] = {
    val insertQ = Pomodoros.map(_.usersId) += Some(usersId)
    db.run(insertQ)
  }

  private val current_timestamp =
    SimpleLiteral[java.sql.Timestamp]("CURRENT_TIMESTAMP")

  def finish(usersId: UUID): Future[Int] = {
    db.run(for {
      timestamp <- current_timestamp.result
      nrUpdated <- Pomodoros
        .filter(_.usersId === usersId)
        .filter(_.finished.isEmpty)
        .map(_.finished)
        .update(Some(timestamp))
    } yield nrUpdated)
  }

  def getStats(usersId: UUID, amount: Int = 5): Future[Seq[PomodoroStats]] = {
    db.run(
      Pomodoros
        .filter(_.usersId === usersId)
        .filter(_.finished.nonEmpty)
        .filter(_.kind === "pomodoro")
        .sortBy(_.finished.desc)
        .take(amount)
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
          .map(r => (r.started, ts, r.kind))
          .result
          .headOption
          .map {
            case None => Idle
            case Some((started, current, "pomodoro")) =>
              Running((current.getTime - started.getTime) / 1000)
            case Some((started, current, "break")) =>
              Break
            case Some((started, current, "long break")) =>
              LongBreak
          }
      } yield result
    )
  }
}
