package repository
package postgres

import java.util.UUID

import model.Pomodoro
import slick.jdbc.JdbcBackend.DatabaseDef
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class PomodoroPqslRepo(implicit db: DatabaseDef, ec: ExecutionContext)
    extends PomodoroRepo {

  private val pomodoroTable = TableQuery[dao.Tables.Pomodoro]

  def start(usersId: UUID) = {
    val insertQ = pomodoroTable.map(_.usersId) += Some(usersId)
    db.run(insertQ)
  }

  def finish(usersId: UUID) = {
    val current_timestamp =
      SimpleLiteral[java.sql.Timestamp]("CURRENT_TIMESTAMP")

    db.run(for {
      timeStamp <- current_timestamp.result
      nrUpdated <- pomodoroTable
        .filter(_.usersId === usersId)
        .filter(_.finished.isEmpty)
        .map(_.finished)
        .update(Some(timeStamp))
    } yield nrUpdated)
  }

  def get(usersId: UUID) = {
    db.run(
      pomodoroTable
        .filter(_.usersId === usersId)
        .filter(_.finished.nonEmpty)
        .sortBy(_.finished.desc)
        .take(5)
        .map(row => (row.started, row.finished.get))
        .result
        .map(_.map(x => Pomodoro.fromInterval(x._1, x._2)))
    )
  }

}
