package repository
package postgres

import java.util.UUID

import scala.concurrent.ExecutionContext
import slick.jdbc.JdbcBackend.DatabaseDef
import slick.jdbc.PostgresProfile.api._

class PasswordRepo(implicit db: DatabaseDef, ec: ExecutionContext) {
  import dao.Tables.{OAuthTokens, GeneratedPasswords}

  def getPasswordByUserId(userId: UUID) = {
    db.run(GeneratedPasswords.filter(_.usersId === userId).result.headOption)
  }

}
