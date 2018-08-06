package endpoints

import java.util.UUID

import scala.concurrent.{ExecutionContext, Future}
import akka.http.scaladsl.server.Directives.authenticateBasicAsync
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.Credentials
import repository.postgres.UserPsqlRepo

class Authentication(implicit userRepo: UserPsqlRepo, ec: ExecutionContext) {

  private def asyncAuthenticator(credentials: Credentials): Future[Option[String]] = {
    credentials match {
      case p@Credentials.Provided(id) if p.verify("1234") =>
        userRepo.findByEmail(id).map(_.map(_.id.toString))

      case _ => Future.successful(None)
    }
  }

  def routeWithAuthentication(route: UUID => Route): Route = {
    authenticateBasicAsync(realm = "pomodoro", asyncAuthenticator) {
      userId =>
        route(UUID.fromString(userId))
    }
  }

}
