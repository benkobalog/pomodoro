package pomodoro.repository
package postgres

import java.util.UUID

import pomodoro.model.GeneratedPassword
import slick.jdbc.JdbcBackend.DatabaseDef
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class PasswordRepo(db: DatabaseDef)(implicit ec: ExecutionContext) {
  import dao.Tables.GeneratedPasswords

  def getPasswordByUserId(userId: UUID): Future[Option[GeneratedPassword]] = {
    db.run(GeneratedPasswords.filter(_.usersId === userId).result.headOption)
  }

}
