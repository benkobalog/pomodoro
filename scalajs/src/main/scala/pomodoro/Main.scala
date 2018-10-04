package pomodoro

import com.thoughtworks.binding.dom
import io.circe.generic.auto._
import io.circe.parser.decode
import org.scalajs.dom.document
import pomodoro.logic._
import pomodoro.model.TokenData

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

  private val settingsF = Settings(httpClient)

  def main(args: Array[String]): Unit = {
    pomodoroStats.getStats.onComplete(println)
  }

  @JSExport
  def renderStats(): Unit =
    dom.render(document.getElementById("stats"), pomodoroStats.stats())

  @JSExport
  def renderSettings(): Unit = {
    println("Render Settings")
    settingsF.foreach(
      settings =>
        dom.render(document.getElementById("settings"),
                   settings.renderSettings()))
  }

  @JSExport
  def renderTimer(): Unit = {
    settingsF.foreach { settings =>
      val uiMediator = new Mediator(settings, pomodoroStats, jwtData.data)

      println("Render Timer")
      dom.render(document.getElementById("timer"), uiMediator.timerHtml())
    }
  }

  private def parseJWT(str: String): String = {
    val base64Url = str.split("\\.")(1)
    val base64 = base64Url.replace('-', '+').replace('_', '/')
    java.util.Base64.getDecoder.decode(base64).map(_.toChar).mkString
  }
}
