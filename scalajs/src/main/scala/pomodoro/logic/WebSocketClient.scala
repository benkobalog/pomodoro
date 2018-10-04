package pomodoro.logic

import io.circe.generic.auto._
import io.circe.syntax._
import org.scalajs.dom
import pomodoro.model.TokenData
import pomodoro.model.wsmessage._
import io.circe.generic.auto._
import io.circe.parser.decode

class WebSocketClient(td: TokenData, mediator: Mediator) {

  private val ws =
    new dom.WebSocket(s"ws://${td.email}:${td.token}@localhost:9002/pomodoro")

  ws.onopen = { e: dom.Event =>
    println("Opened ws connection")
    sendMessage(RequestInit)
  }

  ws.onmessage = { e: org.scalajs.dom.MessageEvent =>
    decode[ControlMessage](e.data.toString) match {
      case Left(err) =>
        println(s"""Failed to decode message: "${e.data.toString}" ::: $err""")

      case Right(cm) =>
        mediator.controlMessageHandler(cm)
    }
  }

  def sendMessage(userRequest: UserRequest): Unit = {
    ws.send(toJson(userRequest))
  }

  private def toJson(userRequest: UserRequest): String =
    userRequest.asJson.noSpaces
}
