package endpoints

import java.util.UUID

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import io.circe.generic.auto._
import io.circe.syntax._
import model.User
import repository.postgres.UserRepo
import utils.implicits.AkkaHttpMarshaller._
import utils.implicits.Circe._

class UserEndpoints(implicit userRepo: UserRepo) {

  def route(userId: UUID): Route = {
    path("user") {
      get {
        onComplete(userRepo.findById(userId))(respond {
          case Some(user) => user.asJson
          case None       => "No such user"
        })
      } ~
        put {
          entity(as[User]) { user =>
            onComplete(userRepo.updateById(user))(respond(_ =>
              "Update Successful"))
          }
        }
    }
  }
}

object UserEndpoints {
  def apply(userId: UUID)(implicit userRepo: UserRepo): Route =
    new UserEndpoints().route(userId)
}
