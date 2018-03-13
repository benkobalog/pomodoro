package repository
package postgres

import model.User
import slick.jdbc.JdbcBackend.DatabaseDef
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext

class UserPSQLRepo(implicit db: DatabaseDef, ec: ExecutionContext)
    extends UserRepo {
  override def findByEmail(email: String) = {

    val q = sql"SELECT * from users".as[(String, String, String)]

    val res = db.run(q)

    Thread.sleep(1000)
    User("asd||", "asd")
  }
}
