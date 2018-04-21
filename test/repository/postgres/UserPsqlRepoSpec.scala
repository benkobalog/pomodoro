package repository.postgres
import org.scalatest._

class UserPsqlRepoSpec extends FlatSpec with Matchers {
  import repository.PostgresConnection.db
  import scala.concurrent.ExecutionContext.Implicits.global
  "UserPsqlRepo" should "find a user based on email" in {
    new UserPsqlRepo()
  }
}
