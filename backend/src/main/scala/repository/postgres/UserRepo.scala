package repository
package postgres

import java.util.UUID

import slick.jdbc.JdbcBackend.DatabaseDef
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}
import model.User

class UserRepo(implicit db: DatabaseDef, ec: ExecutionContext) {
  import dao.Tables.Users

  def updateById(user: User): Future[Int] = {
    val updateQuery = Users.filter(_.id === user.id).map(identity).update(user)
    db.run(updateQuery)
  }

  def findById(userId: UUID): Future[Option[User]] = {
    val userById = Users.filter(_.id === userId)
    db.run(userById.result.headOption)
  }

  def findByEmail(email: String): Future[Option[User]] = {
    val userByEmail = Users.filter(_.email === email)
    db.run(userByEmail.result.headOption)
  }

  def insert(user: User): Future[Int] = {
    db.run(Users += user)
  }

  def insertFromEmail(email: String): Future[Int] = {
    db.run(Users.map(_.email) += email)
  }

  def deleteById(uuid: UUID): Future[Int] = {
    db.run(Users.filter(_.id === uuid).delete)
  }
}
