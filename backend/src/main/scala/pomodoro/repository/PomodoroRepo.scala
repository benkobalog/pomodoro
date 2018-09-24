package pomodoro.repository

import java.util.UUID

import pomodoro.model.PomodoroState

import scala.concurrent.Future

trait PomodoroRepo {
  def start(usersId: UUID, kind: String, started: Double): Future[Int]

  def finish(usersId: UUID): Future[Int]

  def getState(userId: UUID): Future[PomodoroState]
}
