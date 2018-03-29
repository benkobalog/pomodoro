package repository

import java.util.UUID

import scala.concurrent.Future

trait PomodoroRepo {

  def startPomodoro(usersId: UUID): Future[Int]

  def finishPomodoro(usersId: UUID): Future[Int]

}
