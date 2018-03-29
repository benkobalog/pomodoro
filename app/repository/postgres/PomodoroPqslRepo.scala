package repository
package postgres

import java.util.UUID

import slick.jdbc.JdbcBackend.DatabaseDef
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class PomodoroPqslRepo(implicit db: DatabaseDef, ec: ExecutionContext)
    extends PomodoroRepo {

  private val pomodoroTable = TableQuery[dao.Tables.Pomodoro]

  def startPomodoro(usersId: UUID) = {
    val insertQ = pomodoroTable.map(_.usersId) += Some(usersId)
    db.run(insertQ)
  }

  def finishPomodoro(usersId: UUID) = {
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

}
