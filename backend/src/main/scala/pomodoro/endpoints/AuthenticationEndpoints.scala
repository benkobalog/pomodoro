package pomodoro.endpoints

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._

class AuthenticationEndpoints(implicit authentication: Authentication) {

  def route: Route = {
    path("authenticate") {
      get {
        complete("")
      }
    }
  }
}

object AuthenticationEndpoints {
  def apply()(implicit authentication: Authentication): Route =
    new AuthenticationEndpoints().route
}
