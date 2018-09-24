package pomodoro.repository

import java.util.UUID

import pomodoro.model.PomodoroStats

import scala.concurrent.Future

trait PomodoroStatsRepo {
  def getStats(usersId: UUID, amount: Int = 5): Future[Seq[PomodoroStats]]
}
