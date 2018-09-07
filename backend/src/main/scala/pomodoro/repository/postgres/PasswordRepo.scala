package pomodoro.repository
package postgres

import java.util.UUID

import pomodoro.model.GeneratedPassword

import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcBackend.DatabaseDef
import slick.jdbc.PostgresProfile.api._

class PasswordRepo(implicit db: DatabaseDef, ec: ExecutionContext) {
  import dao.Tables.{OAuthTokens, GeneratedPasswords}

  def getPasswordByUserId(userId: UUID): Future[Option[GeneratedPassword]] = {
    db.run(GeneratedPasswords.filter(_.usersId === userId).result.headOption)
  }

}
