package endpoints

import akka.http.scaladsl.server.Directives.authenticateBasic
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.Credentials

object Authentication {

  private def userPassAuthenticator(credentials: Credentials): Option[String] = {
    credentials match {
      case p@Credentials.Provided(id) if p.verify("1234") => Some(id)
      case _ => None
    }
  }


  def routeWithAuthenitcation(route: Route): Route = {
    authenticateBasic(realm = "pomodoro", userPassAuthenticator) {
      userName =>
        route
    }
  }

}
