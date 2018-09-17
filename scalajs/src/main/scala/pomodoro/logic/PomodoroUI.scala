package pomodoro.logic

import com.thoughtworks.binding.Binding.{BindingSeq, Var}
import com.thoughtworks.binding.{Binding, dom}
import io.circe.parser.decode
import io.circe.generic.auto._
import org.scalajs.dom.raw.Node
import pomodoro.model._
import pomodoro.model.wsmessage.{EndPomodoro, StartPomodoro}

import scala.scalajs.js.timers.{SetIntervalHandle, clearInterval, setInterval}

class PomodoroUI(user: User, wsClient: WebSocketClient) {

  private val timeResolution = 1

  var timer: Option[SetIntervalHandle] = None

  var timerSeconds: Var[Int] = Var(user.pomodoroSeconds)

  private def createTimer(seconds: Int): Unit = {
    timer.foreach(clearInterval)

    var elapsedSeconds = 0
    timer = Some(setInterval(timeResolution * 1000) {
      timerSeconds.value -= timeResolution
      if (elapsedSeconds >= seconds) {
        timer.foreach(clearInterval)
      }
      elapsedSeconds += timeResolution
    })
  }

  wsClient.setMessageHandler { e: org.scalajs.dom.MessageEvent =>
    decode[PomodoroState](e.data.toString) match {
      case Left(err) =>
        println(s"Failed to decode message: ${e.data.toString} ::: $err")
      case Right(ps) =>
        println(s"Message: $ps")
        updateState(ps)
    }
  }

  private def toIdle(): Unit = {
    timer.foreach(clearInterval)
    timerSeconds.value = user.pomodoroSeconds
  }

  @dom def timerHtml(): Binding[BindingSeq[Node]] = {
    <div class="row">
      <div class="col">
        <div class="card" id="pomodoro-timer-wrapper">
          <h2>
            <div class="card-body text-center" id="pomodoro-timer">
              {secondsToTime(timerSeconds.bind)}
            </div>
          </h2>
        </div>
      </div>
    </div>
      <div class="row">
        <div class="col">
          <button type="button"
                  class="btn btn-outline-success btn-lg btn-block"
                  id="start-button"
                  onclick={ e: org.scalajs.dom.Event =>
                    wsClient.sendMessage(StartPomodoro)
                  }
          >Start</button>
        </div>
        <div class="col">
          <button type="button"
                  class="btn btn-outline-danger btn-lg btn-block"
                  id="stop-button"
                  onclick={ e: org.scalajs.dom.Event =>
                    wsClient.sendMessage(EndPomodoro)
                  }
          >Stop</button>
        </div>
      </div>
  }

  private def updateState(pomodoroState: PomodoroState): Unit =
    pomodoroState match {
      case Idle =>
        toIdle()
      case Break(kind, started) =>
        timerSeconds.value = user.breakSeconds
        createTimer(user.breakSeconds)
      case Running(started) =>
        timerSeconds.value = user.pomodoroSeconds
        createTimer(user.pomodoroSeconds)
    }

  private def secondsToTime(seconds: Long): String = {
    val h = seconds / 3600
    val m = seconds % 3600 / 60
    val s = seconds % 3600 % 60

    f"${if (h == 0) "" else h + ":"}$m%02d:$s%02d"
  }
}
