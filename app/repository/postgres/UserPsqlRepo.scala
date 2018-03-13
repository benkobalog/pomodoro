package repository
package postgres

import repository.dao.Tables
import slick.jdbc.JdbcBackend.DatabaseDef
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class UserPsqlRepo(implicit db: DatabaseDef, ec: ExecutionContext)
    extends UserRepo {
  def findByEmail(email: String): Future[Option[Tables.UsersRow]] = {
    val tableQuery = TableQuery[dao.Tables.Users]
    val userByEmail = tableQuery.filter(_.email === email)
    db.run(userByEmail.result.headOption)
  }
}
