package repository

import slick.jdbc.JdbcBackend
import slick.jdbc.JdbcBackend.Database
import slick.driver.PostgresDriver

object PostgresConnection {

  implicit val db: JdbcBackend.Database = Database.forConfig("postgres")

  def close(): Unit = db.close()
}
