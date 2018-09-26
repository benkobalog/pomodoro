package pomodoro.logic

import com.thoughtworks.binding.Binding.{BindingSeq, Var}
import com.thoughtworks.binding.{Binding, dom}
import io.circe.generic.auto._
import io.circe.parser.decode
import org.scalajs.dom.Event
import org.scalajs.dom.document
import org.scalajs.dom.raw.{HTMLAudioElement, HTMLMediaElement, Node}
import pomodoro.model._
import pomodoro.model.wsmessage._

import scala.scalajs.js
import scala.scalajs.js.Date
import scala.scalajs.js.annotation.JSGlobal
import scala.scalajs.js.timers.{SetIntervalHandle, clearInterval, setInterval}

class PomodoroUI(settings: Settings,
                 wsClient: WebSocketClient,
                 pStats: PomodoroStatistics) {

  private var clockOffset: Double = 0
  private def currentClientTime: Double = Date.now()
  private def syncTime(): Double = Date.now() + clockOffset

  wsClient.setMessageHandler { e: org.scalajs.dom.MessageEvent =>
    decode[ControlMessage](e.data.toString) match {
      case Left(err) =>
        println(s"""Failed to decode message: "${e.data.toString}" ::: $err""")

      case Right(ClockSync(serverTime)) =>
        clockOffset = currentClientTime - serverTime
        println(s"Offset set to: $clockOffset")

      case Right(State(ps)) =>
        println(s"Message: $ps")
        updateState(ps)
    }
  }

  val sound = document.getElementById("audio").asInstanceOf[HTMLAudioElement]

  case class ButtonProps(disabled: String,
                         text: String,
                         color: String,
                         onClick: Event => Unit)

  case class Controls(left: ButtonProps, right: ButtonProps)

  private val idleButtons = Controls(
    ButtonProps("",
                "Start Pomodoro",
                "btn-outline-success",
                _ => wsClient.sendMessage(StartPomodoro)),
    ButtonProps("d-none", "Stop", "btn-outline-danger", _ => ())
  )

  private val runningButtons = Controls(
    ButtonProps("",
                "Start Break",
                "btn-outline-warning",
                _ => wsClient.sendMessage(StartBreak("break"))),
    ButtonProps("",
                "Stop Pomodoro",
                "btn-outline-danger",
                _ => wsClient.sendMessage(EndPomodoro))
  )

  private val breakButtons = Controls(
    ButtonProps("",
                "Stop Break",
                "btn-outline-warning",
                _ => wsClient.sendMessage(EndBreak)),
    ButtonProps("d-none", "Stop Pomodoro", "btn-outline-danger", _ => ())
  )

  private val timeResolution = 1
  private var timer: Option[SetIntervalHandle] = None
  private val timerSeconds: Var[Int] = Var(settings.getUser.pomodoroSeconds)
  private val stopButtonProps: Var[ButtonProps] = Var(idleButtons.left)
  private val startButtonProps: Var[ButtonProps] = Var(idleButtons.right)

  private def createTimer(seconds: Int): Unit = {
    timer.foreach(clearInterval)

    var elapsedSeconds = 0
    timer = Some(setInterval(timeResolution * 1000) {
      timerSeconds.value -= timeResolution
      elapsedSeconds += timeResolution
      if (elapsedSeconds >= seconds) {
        timer.foreach(clearInterval)
        sound.play()
      }
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

  private def doNothing(a: Any): Unit = ()
  private def updateTimerValue(seconds: Int): Unit =
    timerSeconds.value = seconds

  private def updateState(pomodoroState: PomodoroState): Unit = {
    pomodoroState match {
      case Idle =>
        settings.setSaveButtonEvent(updateTimerValue)
        timer.foreach(clearInterval)
        timerSeconds.value = settings.getUser.pomodoroSeconds
        startButtonProps.value = idleButtons.left
        stopButtonProps.value = idleButtons.right

      case Break(kind, started) =>
        settings.setSaveButtonEvent(doNothing)
        val secondsLeft =
          (settings.getUser.breakSeconds - (syncTime - started) / 1000).toInt
        timerSeconds.value = secondsLeft
        createTimer(secondsLeft)
        startButtonProps.value = breakButtons.left
        stopButtonProps.value = breakButtons.right

      case Running(started) =>
        settings.setSaveButtonEvent(doNothing)
        val secondsLeft =
          (settings.getUser.pomodoroSeconds - (syncTime - started) / 1000).toInt
        timerSeconds.value = secondsLeft
        createTimer(secondsLeft)
        startButtonProps.value = runningButtons.left
        stopButtonProps.value = runningButtons.right
    }
    pStats.getStats
  }

  private def showTime(seconds: Long): String = {
    val (h, m, s) = secondsToTime(seconds)
    f"${if (h == 0) "" else h + ":"}$m%02d:$s%02d"
  }
}
