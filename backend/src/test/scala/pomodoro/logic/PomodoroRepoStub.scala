package pomodoro.logic

import java.util.UUID

import pomodoro.model._
import pomodoro.repository.{PomodoroRepo, PomodoroStatsRepo}

import scala.concurrent.Future

class PomodoroRepoStub extends PomodoroRepo {
  override def start(usersId: UUID): Future[Int] = {
    Future.successful(1)
  }

  override def finish(usersId: UUID): Future[Int] = {
    Future.successful(1)
  }

  override def getState(usersId: UUID): Future[PomodoroState] = {
    Future.successful(Idle)
  }
}

class PomodoroStatsRepoStub extends PomodoroStatsRepo {
  override def getStats(usersId: UUID, amount: Int): Future[Seq[PomodoroStats]] =
    Future.successful(Seq())
}