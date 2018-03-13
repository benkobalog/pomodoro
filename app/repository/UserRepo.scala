package repository

import repository.dao.Tables

import scala.concurrent.Future

trait UserRepo {
  def findByEmail(email: String): Future[Option[Tables.UsersRow]]
}
