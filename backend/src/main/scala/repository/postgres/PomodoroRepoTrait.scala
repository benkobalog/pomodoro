package repository.postgres

import java.util.UUID

import model.{PomodoroState, PomodoroStats}

import scala.concurrent.Future

trait PomodoroRepoTrait {
  def start(usersId: UUID): Future[Int]

  def finish(usersId: UUID): Future[Int]

  def getStats(usersId: UUID, amount: Int = 5): Future[Seq[PomodoroStats]]

  def getState(usersId: UUID): Future[PomodoroState]
}
