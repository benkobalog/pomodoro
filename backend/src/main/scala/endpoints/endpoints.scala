import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes.InternalServerError
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.StandardRoute

import scala.util.{Failure, Success, Try}

package object endpoints {
  private[endpoints] def respond[A](fn: A => ToResponseMarshallable)(
      body: Try[A]): StandardRoute = body match {
    case Success(value) => complete(fn(value))
    case Failure(err) =>
      println("Error happened: " + err.toString)
      complete(InternalServerError, "Internal Server Error")
  }
}
