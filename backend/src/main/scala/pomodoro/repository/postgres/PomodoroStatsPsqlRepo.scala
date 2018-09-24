package pomodoro.repository
package postgres

import java.util.UUID

import pomodoro.model.{Pomodoro, PomodoroStats}
import slick.jdbc.JdbcBackend.DatabaseDef
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class PomodoroStatsPsqlRepo(db: DatabaseDef)(implicit ec: ExecutionContext)
    extends PomodoroStatsRepo {

  import dao.Tables.Pomodoros

  override def getStats(usersId: UUID,
                        amount: Int = 5): Future[Seq[PomodoroStats]] = {
    db.run(
      Pomodoros
        .filter(_.usersId === usersId)
        .filter(_.kind === "pomodoro")
        .sortBy(_.finished.desc)
        .take(amount)
        .map(row => (row.started, row.finished))
        .result
        .map(_.map(x => PomodoroStats(x._1, x._2)))
    )
  }
}
