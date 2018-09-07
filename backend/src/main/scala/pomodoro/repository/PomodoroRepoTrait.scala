package pomodoro.repository

import java.util.UUID

import pomodoro.model.PomodoroState

import scala.concurrent.Future

trait PomodoroRepoTrait {
  def start(usersId: UUID): Future[Int]

  def finish(usersId: UUID): Future[Int]

  def getState(usersId: UUID): Future[PomodoroState]
}
