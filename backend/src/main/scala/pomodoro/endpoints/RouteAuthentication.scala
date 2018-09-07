package pomodoro.endpoints

import java.util.UUID

import scala.concurrent.{ExecutionContext, Future}
import akka.http.scaladsl.server.Directives.authenticateBasicAsync
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.Credentials

class RouteAuthentication(implicit authentication: Authentication,
                          ec: ExecutionContext) {

  private def asyncAuthenticator(
      credentials: Credentials): Future[Option[UUID]] = {
    credentials match {
      case p @ Credentials.Provided(email) =>
        authentication
          .getPasswordByEmail(email)
          .map(_.flatMap(pw =>
            if (p.verify(pw.password)) Some(pw.userId) else None))

      case _ => Future.successful(None)
    }
  }

  def routeWithAuthentication(route: UUID => Route): Route = {
    authenticateBasicAsync(realm = "pomodoro", asyncAuthenticator) { userId =>
      route(userId)
    }
  }

}
