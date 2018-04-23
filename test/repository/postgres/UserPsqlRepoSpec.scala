package repository.postgres
import java.sql.Timestamp
import java.util.{Calendar, UUID}

import org.scalatest._
import repository.dao.Tables.UsersRow
import repository.PostgresConnection.db

class UserPsqlRepoSpec extends AsyncFlatSpec with Matchers {

  private val repo = new UserPsqlRepo()
  private val timestamp = new Timestamp(Calendar.getInstance().getTime.getTime)
  private val email = "test@user.com"
  private val user = UsersRow(UUID.randomUUID(), email, timestamp)

  "UserPsqlRepo" should "insert delete user" in {
    for {
      _ <- repo.insert(user)
      retrievedUser <- repo.findByEmail(email)
      delete <- repo.deleteById(retrievedUser.get.id)
    } yield assert(delete == 1 && retrievedUser.get == user)
  }

  "UserPsqlRepo" should "insertByEmail delete user" in {
    for {
      _ <- repo.insertFromEmail(user.email)
      retrievedUser <- repo.findByEmail(email)
      delete <- repo.deleteById(retrievedUser.get.id)
    } yield assert(delete == 1 && retrievedUser.get.email == user.email)
  }
}
