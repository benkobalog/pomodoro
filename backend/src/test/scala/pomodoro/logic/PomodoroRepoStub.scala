package pomodoro.logic

import java.util.UUID

import pomodoro.model._
import pomodoro.repository.PomodoroRepoTrait

import scala.concurrent.Future

class PomodoroRepoStub extends PomodoroRepoTrait {
  override def start(usersId: UUID): Future[Int] = {
    Future.successful(1)
  }

  override def finish(usersId: UUID): Future[Int] = {
    Future.successful(1)
  }

  override def getStats(usersId: UUID,
                        amount: Int = 5): Future[Seq[PomodoroStats]] = {
    Future.successful(Seq())
  }

  override def getState(usersId: UUID): Future[PomodoroState] = {
    Future.successful(Idle)
  }
}
