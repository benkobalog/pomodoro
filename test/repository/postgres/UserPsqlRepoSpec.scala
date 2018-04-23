package repository.postgres
import java.sql.Timestamp
import java.util.{Calendar, UUID}

import org.scalatest._
import repository.dao.Tables.UsersRow
import repository.PostgresConnection.db

class UserPsqlRepoSpec extends AsyncFlatSpec with Matchers {

  "UserPsqlRepo" should "insert delete user" in {
    val repo = new UserPsqlRepo()
    val timestamp = new Timestamp(Calendar.getInstance().getTime.getTime)
    val email = "test@user.com"
    val user = UsersRow(UUID.randomUUID(), email, timestamp)

    for {
      _ <- repo.insertUser(user)
      retrievedUser <- repo.findByEmail(email)
      delete <- repo.deleteById(retrievedUser.get.id)
    } yield assert(delete == 1 && retrievedUser.get == user)
  }
}
