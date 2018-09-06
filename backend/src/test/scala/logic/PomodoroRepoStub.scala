package logic

import repository.postgres.PomodoroRepoTrait
import model._

import scala.concurrent.Future
import java.util.UUID

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
    Future.successful(Break)
  }
}
