package repository

import java.util.UUID

import scala.concurrent.Future

trait PomodoroRepo {

  def start(usersId: UUID): Future[Int]

  def finish(usersId: UUID): Future[Int]

}
