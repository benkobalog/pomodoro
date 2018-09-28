package pomodoro.logic

import com.thoughtworks.binding.Binding.{BindingSeq, Var}
import com.thoughtworks.binding.{Binding, dom}
import io.circe.generic.auto._
import io.circe.parser.decode
import org.scalajs.dom.document
import org.scalajs.dom.raw.{HTMLAudioElement, Node}
import pomodoro.model._
import pomodoro.model.wsmessage._

import scala.scalajs.js.Date
import scala.scalajs.js.timers.{SetIntervalHandle, clearInterval, setInterval}

class PomodoroUI(settings: Settings,
                 wsClient: WebSocketClient,
                 pStats: PomodoroStatistics) {

  private val time = new SyncTime()

  wsClient.setMessageHandler { e: org.scalajs.dom.MessageEvent =>
    decode[ControlMessage](e.data.toString) match {
      case Left(err) =>
        println(s"""Failed to decode message: "${e.data.toString}" ::: $err""")

      case Right(ClockSync(serverTime)) =>
        time.setTime(serverTime)
        println(s"Offset set to: ${time.getOffsetMillis} ms")

      case Right(State(ps)) =>
        println(s"Message: $ps")
        updateState(ps)
    }
  }

  private val buttonStates = new ButtonStates(wsClient)

  val sound = document.getElementById("audio").asInstanceOf[HTMLAudioElement]

  private val timeResolution = 1
  private var timer: Option[SetIntervalHandle] = None
  private val timerSeconds: Var[Int] = Var(settings.getUser.pomodoroSeconds)
  private val stopButtonProps: Var[ButtonProps] = Var(buttonStates.idle.left)
  private val startButtonProps: Var[ButtonProps] = Var(buttonStates.idle.right)

  private def createDownTimer(seconds: Int): Unit = {
    timer.foreach(clearInterval)

    var elapsedSeconds = 0
    timer = Some(setInterval(timeResolution * 1000) {
      timerSeconds.value -= timeResolution
      elapsedSeconds += timeResolution
      if (elapsedSeconds >= seconds) {
        if (settings.getUser.autoStartBreak) {
          timer.foreach(clearInterval)
        }
//        sound.play()
        updateState(RunningOvertime(Date.now() - seconds * 1000))
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

  private def updateTimerValue(seconds: Int): Unit =
    timerSeconds.value = seconds

  private def updateState(pomodoroState: PomodoroState): Unit = {
    pomodoroState match {
      case Idle =>
        settings.setSaveButtonEvent(Some(updateTimerValue))
        timer.foreach(clearInterval)
        timerSeconds.value = settings.getUser.pomodoroSeconds
        startButtonProps.value = buttonStates.idle.left
        stopButtonProps.value = buttonStates.idle.right

      case Break(kind, started) =>
        settings.setSaveButtonEvent(None)
        val secondsLeft =
          (settings.getUser.breakSeconds - (time.getMillis - started) / 1000).toInt
        timerSeconds.value = secondsLeft
        createDownTimer(secondsLeft)
        startButtonProps.value = buttonStates.break.left
        stopButtonProps.value = buttonStates.break.right

      case Running(started) =>
        settings.setSaveButtonEvent(None)
        val secondsLeft =
          (settings.getUser.pomodoroSeconds - (time.getMillis - started) / 1000).toInt
        timerSeconds.value = secondsLeft
        createDownTimer(secondsLeft)
        startButtonProps.value = buttonStates.running.left
        stopButtonProps.value = buttonStates.running.right

      case RunningOvertime(started) =>
        settings.setSaveButtonEvent(None)
        startButtonProps.value = buttonStates.runningOvertime.left
        stopButtonProps.value = buttonStates.runningOvertime.right

    }
    pStats.getStats
  }

  private def showTime(seconds: Long): String = {
    val (h, m, s) = secondsToTime(seconds)
    f"${if (h == 0) "" else h + ":"}$m%02d:$s%02d"
  }
}
