package pomodoro

import com.softwaremill.sttp.{FetchBackend, SttpBackend}
import pomodoro.model.TokenData
import com.softwaremill.sttp._
import io.circe.Decoder

import scala.concurrent.Future
import io.circe.parser.decode
import scala.concurrent.ExecutionContext.Implicits.global

case class HttpClient(tokenData: TokenData) {
  private implicit val sttpBackend: SttpBackend[Future, Nothing] =
    FetchBackend()

  def get[A](url: String)(implicit decoder: Decoder[A]): Future[A] = {
    sttp
      .get(uri"$url")
      .auth
      .basic(tokenData.email, tokenData.token)
      .send
      .map(response =>
        for {
          string <- response.body.left.map(new Exception(_))
          _ = println(string)
          result <- decode[A](string)
        } yield result)
      .flatMap(x => Future.fromTry(x.toTry))
  }

}
