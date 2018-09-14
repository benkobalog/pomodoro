package pomodoro

import org.scalajs.dom.document
import pomodoro.model.{PomodoroStats, TokenData}
import io.circe.parser.decode
import io.circe.generic.auto._
import pomodoro.logic.PomodoroStatistics
import scala.concurrent.ExecutionContext.Implicits.global

case class JWTData(data: TokenData)

object Main {

  def main(args: Array[String]): Unit = {
    val jwtData =
      decode[JWTData](parseJWT(document.cookie))
        .getOrElse(throw new Exception("We should have a cookie at this point"))

    val httpClient = HttpClient(jwtData.data)
    val pomodoroStats = new PomodoroStatistics(httpClient)
    pomodoroStats.getStats.onComplete(println)
  }

  private def parseJWT(str: String): String = {
    val base64Url = str.split("\\.")(1)
    val base64 = base64Url.replace('-', '+').replace('_', '/')
    java.util.Base64.getDecoder.decode(base64).map(_.toChar).mkString
  }
}
