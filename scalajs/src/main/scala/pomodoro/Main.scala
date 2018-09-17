package pomodoro

import com.thoughtworks.binding.dom
import io.circe.generic.auto._
import io.circe.parser.decode
import pomodoro.logic.{PomodoroStatistics, PomodoroUI, WebSocketClient}
import pomodoro.model.{TokenData, User}
import org.scalajs.dom.document

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

import com.softwaremill.macwire._

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
  }

  @JSExport
  def renderStats(): Unit =
    dom.render(document.getElementById("stats"), pomodoroStats.stats())

  @JSExport
  def renderTimer(): Unit = {
    httpClient
      .get[User]("http://localhost:9001/user")
      .foreach { user =>
        val wsClient = new WebSocketClient(jwtData.data)
        val ui = new PomodoroUI(user, wsClient)

        dom.render(document.getElementById("timer"), ui.timerHtml())
      }
  }

  private def parseJWT(str: String): String = {
    val base64Url = str.split("\\.")(1)
    val base64 = base64Url.replace('-', '+').replace('_', '/')
    java.util.Base64.getDecoder.decode(base64).map(_.toChar).mkString
  }
}
