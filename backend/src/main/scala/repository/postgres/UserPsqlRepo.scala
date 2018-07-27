package repository
package postgres

import java.util.UUID

import slick.jdbc.JdbcBackend.DatabaseDef
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}
import dao.Tables.{Users, UsersRow}

class UserPsqlRepo(implicit db: DatabaseDef, ec: ExecutionContext) {
  private val tableQuery = TableQuery[Users]

  def findByEmail(email: String): Future[Option[UsersRow]] = {
    val userByEmail = tableQuery.filter(_.email === email)
    db.run(userByEmail.result.headOption)
  }

  def insert(user: UsersRow): Future[Int] = {
    db.run(tableQuery += user)
  }

  def insertFromEmail(email: String): Future[Int] = {
    db.run(tableQuery.map(_.email) += email)
  }

  def deleteById(uuid: UUID): Future[Int] = {
    db.run(tableQuery.filter(_.id === uuid).delete)
  }
}
