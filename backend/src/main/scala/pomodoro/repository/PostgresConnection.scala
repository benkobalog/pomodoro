package pomodoro.repository

import slick.jdbc.JdbcBackend
import slick.jdbc.JdbcBackend.Database

object PostgresConnection {

  implicit val db: JdbcBackend.Database = Database.forConfig("postgres")

  def close(): Unit = db.close()
}
