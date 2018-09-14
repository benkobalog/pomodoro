package pomodoro.logic

import org.scalajs.dom
import pomodoro.model.TokenData
import pomodoro.model.wsmessage.{RequestInit, UserRequest}
import io.circe.generic.auto._
import io.circe.syntax._

class PomodoroWS(td: TokenData) {

  private val ws =
    new dom.WebSocket(s"ws://${td.email}:${td.token}@localhost:9002/pomodoro")

  ws.onopen = { e: dom.Event =>
    println("Opened ws")
    ws.send(toJson(RequestInit))
  }

  ws.onmessage = { e: dom.MessageEvent =>
    println(e.data.toString)
    ws.send("FUck")
  }

  private def toJson(userRequest: UserRequest): String = userRequest.asJson.noSpaces

  def sendMessage(userRequest: UserRequest): Unit = {
    ws.send(toJson(userRequest))
  }

}
