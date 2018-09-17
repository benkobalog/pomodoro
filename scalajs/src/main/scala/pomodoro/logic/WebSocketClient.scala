package pomodoro.logic

import io.circe.generic.auto._
import io.circe.syntax._
import org.scalajs.dom
import pomodoro.model.TokenData
import pomodoro.model.wsmessage.{RequestInit, UserRequest}

class WebSocketClient(td: TokenData) {

  private val ws =
    new dom.WebSocket(s"ws://${td.email}:${td.token}@localhost:9002/pomodoro")

  ws.onopen = { e: dom.Event =>
    println("Opened ws connection")
    sendMessage(RequestInit)
  }

  def setMessageHandler(fn: dom.MessageEvent => Unit): Unit =
    ws.onmessage = fn

  def sendMessage(userRequest: UserRequest): Unit = {
    ws.send(toJson(userRequest))
  }

  private def toJson(userRequest: UserRequest): String =
    userRequest.asJson.noSpaces
}
