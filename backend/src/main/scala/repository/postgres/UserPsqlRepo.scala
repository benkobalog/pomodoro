package repository
package postgres

import java.util.UUID

import slick.jdbc.JdbcBackend.DatabaseDef
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}
import model.User

class UserPsqlRepo(implicit db: DatabaseDef, ec: ExecutionContext) {
  import dao.Tables.Users

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
