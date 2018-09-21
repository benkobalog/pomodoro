package pomodoro

import com.softwaremill.sttp.{FetchBackend, SttpBackend, _}
import io.circe.parser.decode
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import pomodoro.model.TokenData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

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
          result <- decode[A](string)
        } yield result)
      .flatMap(x => Future.fromTry(x.toTry))
  }

  def put[A](url: String)(a: A)(implicit encoder: Encoder[A]): Future[Response[String]] =
    sttp
      .put(uri"$url")
      .body(a.asJson.noSpaces)
      .contentType("application/json")
      .auth
      .basic(tokenData.email, tokenData.token)
      .send()

}
