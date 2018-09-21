package pomodoro.logic

import com.thoughtworks.binding.Binding.{BindingSeq, Var}
import com.thoughtworks.binding.{Binding, dom}
import io.circe.generic.auto._
import io.circe.parser.decode
import org.scalajs.dom.Event
import org.scalajs.dom.raw.Node
import pomodoro.model._
import pomodoro.model.wsmessage._

import scala.scalajs.js.timers.{SetIntervalHandle, clearInterval, setInterval}

class PomodoroUI(settings: Settings, wsClient: WebSocketClient, pStats: PomodoroStatistics) {

  wsClient.setMessageHandler { e: org.scalajs.dom.MessageEvent =>
    decode[PomodoroState](e.data.toString) match {
      case Left(err) =>
        println(s"""Failed to decode message: "${e.data.toString}" ::: $err""")
      case Right(ps) =>
        println(s"Message: $ps")
        updateState(ps)
    }
  }

  case class ButtonProps(disabled: String,
                         text: String,
                         color: String,
                         onClick: Event => Unit)

  private val idleButtons = (
    ButtonProps("",
                "Start Pomodoro",
                "btn-outline-success",
                _ => wsClient.sendMessage(StartPomodoro)),
    ButtonProps("d-none", "Stop", "btn-outline-danger", _ => ())
  )

  private val runningButtons = (
    ButtonProps("",
                "Start Break",
                "btn-outline-warning",
                _ => wsClient.sendMessage(StartBreak("break"))),
    ButtonProps("",
                "Stop Pomodoro",
                "btn-outline-danger",
                _ => wsClient.sendMessage(EndPomodoro))
  )

  private val breakButtons = (
    ButtonProps("",
                "Stop Break",
                "btn-outline-warning",
                _ => wsClient.sendMessage(EndBreak)),
    ButtonProps("d-none", "Stop Pomodoro", "btn-outline-danger", _ => ())
  )

  private val timeResolution = 1
  private var timer: Option[SetIntervalHandle] = None
  private val timerSeconds: Var[Int] = Var(settings.user.pomodoroSeconds)
  private val stopButtonProps: Var[ButtonProps] = Var(idleButtons._1)
  private val startButtonProps: Var[ButtonProps] = Var(idleButtons._2)

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

  @dom def timerHtml(): Binding[BindingSeq[Node]] = {
    <div class="row">
      <div class="col">
        <div class="card" id="pomodoro-timer-wrapper">
          <h2>
            <div class="card-body text-center" id="pomodoro-timer">
              {showTime(timerSeconds.bind)}
            </div>
          </h2>
        </div>
      </div>
    </div>
    <div class="row">
      <div class="col">
        <button
          type="button"
          class={"btn " + startButtonProps.bind.color + " btn-lg btn-block " + startButtonProps.bind.disabled}
          id="start-button"
          onclick={startButtonProps.bind.onClick}
        >{startButtonProps.bind.text}</button>
      </div>
      <div class="col">
        <button
          type="button"
          class={"btn " + stopButtonProps.bind.color + " btn-lg btn-block " + stopButtonProps.bind.disabled}
          id="stop-button"
          onclick={stopButtonProps.bind.onClick}
        >{stopButtonProps.bind.text}</button>
      </div>
    </div>
  }

  private def updateState(pomodoroState: PomodoroState): Unit =
    pomodoroState match {
      case Idle =>
        pStats.getStats
        timer.foreach(clearInterval)
        timerSeconds.value = settings.user.pomodoroSeconds
        startButtonProps.value = idleButtons._1
        stopButtonProps.value = idleButtons._2

      case Break(kind, started) =>
        pStats.getStats
        timerSeconds.value = settings.user.breakSeconds
        createTimer(settings.user.breakSeconds)
        startButtonProps.value = breakButtons._1
        stopButtonProps.value = breakButtons._2

      case Running(started) =>
        pStats.getStats
        timerSeconds.value = settings.user.pomodoroSeconds
        createTimer(settings.user.pomodoroSeconds)
        startButtonProps.value = runningButtons._1
        stopButtonProps.value = runningButtons._2
    }

  private def showTime(seconds: Long): String = {
    val (h, m, s) = secondsToTime(seconds)
    f"${if (h == 0) "" else h + ":"}$m%02d:$s%02d"
  }
}
