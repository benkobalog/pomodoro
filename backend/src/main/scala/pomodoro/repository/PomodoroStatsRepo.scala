package pomodoro.repository

import java.util.UUID

import pomodoro.model.PomodoroStats

import scala.concurrent.Future

trait PomodoroStatsRepo {
  def add(started: Double, finished: Double, userId: UUID): Future[Int]
  def getStats(usersId: UUID, amount: Int = 5): Future[Seq[PomodoroStats]]
}
