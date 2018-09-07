package pomodoro.endpoints

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._

class AuthenticationEndpoints(authentication: Authentication) {

  def route: Route = {
    path("authenticate") {
      get {
        complete("")
      }
    }
  }
}