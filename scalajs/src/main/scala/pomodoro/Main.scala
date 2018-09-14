package pomodoro

import com.thoughtworks.binding.dom
import io.circe.generic.auto._
import io.circe.parser.decode
import org.scalajs.dom.document
import pomodoro.logic.{PomodoroStatistics, PomodoroWS}
import pomodoro.model.TokenData
import org.scalajs.dom.document

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

case class JWTData(data: TokenData)

@JSExportTopLevel("Main")
object Main {
  private val jwtData =
    decode[JWTData](parseJWT(document.cookie))
      .getOrElse(throw new Exception("We should have a cookie at this point"))

  private val httpClient = HttpClient(jwtData.data)
  private val pomodoroStats = new PomodoroStatistics(httpClient)

  def main(args: Array[String]): Unit = {
    pomodoroStats.getStats.onComplete(println)
    new PomodoroWS(jwtData.data)
  }

  @JSExport
  def render() =
    dom.render(document.getElementById("testmyshit"), pomodoroStats.stats)

  private def parseJWT(str: String): String = {
    val base64Url = str.split("\\.")(1)
    val base64 = base64Url.replace('-', '+').replace('_', '/')
    java.util.Base64.getDecoder.decode(base64).map(_.toChar).mkString
  }
}
