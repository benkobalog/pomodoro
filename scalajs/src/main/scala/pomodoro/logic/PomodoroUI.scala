package pomodoro.logic

import com.thoughtworks.binding.Binding.{BindingSeq, Var}
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.Node
import org.scalajs.dom.document
import pomodoro.model._
import pomodoro.model.wsmessage._

import scala.scalajs.js.timers.clearInterval

class PomodoroUI(settings: Settings,
                 buttonStates: ButtonStates,
                 mediator: Mediator) {

  private val timerSeconds: Var[Int] = mediator.timer.getTimerVar
  private val time = new SyncTime()

  var currentState: PomodoroState = Idle

  def controlMessageHandler(controlMessage: ControlMessage): Unit =
    controlMessage match {
      case ClockSync(serverTime) =>
        time.synchroniseTime(serverTime)
        println(s"Offset set to: ${time.getOffsetMillis} ms")

      case State(ps) =>
        println(s"Message: $ps")
        currentState = ps
        updateState(ps)
    }

//  val sound = document.getElementById("audio").asInstanceOf[HTMLAudioElement]

  private val stopButtonProps: Var[ButtonProps] = Var(buttonStates.idle.left)
  private val startButtonProps: Var[ButtonProps] = Var(buttonStates.idle.right)

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

  private def updateState(pomodoroState: PomodoroState): Unit =
    pomodoroState match {
      case Idle =>
        settings.setSaveButtonEvent(Some(updateTimerValue))
        mediator.clearTimer()
        timerSeconds.value = settings.getUser.pomodoroSeconds
        startButtonProps.value = buttonStates.idle.left
        stopButtonProps.value = buttonStates.idle.right

      case Break(kind, started) =>
        settings.setSaveButtonEvent(None)
        val secondsLeft =
          (settings.getUser.breakSeconds - (time.getMillis - started) / 1000).toInt
        timerSeconds.value = secondsLeft
        mediator.createTimer(secondsLeft)
        startButtonProps.value = buttonStates.break.left
        stopButtonProps.value = buttonStates.break.right

      case Running(started) =>
        settings.setSaveButtonEvent(None)
        val secondsLeft =
          (settings.getUser.pomodoroSeconds - (time.getMillis - started) / 1000).toInt
        timerSeconds.value = secondsLeft
        mediator.createTimer(secondsLeft)
        startButtonProps.value = buttonStates.running.left
        stopButtonProps.value = buttonStates.running.right

      case RunningOvertime(started) =>
        settings.setSaveButtonEvent(None)
        startButtonProps.value = buttonStates.runningOvertime.left
        stopButtonProps.value = buttonStates.runningOvertime.right

      case BreakOvertime(_, started) =>
        settings.setSaveButtonEvent(None)
        startButtonProps.value = buttonStates.breakOvertime.left
        stopButtonProps.value = buttonStates.breakOvertime.right

    }

  private def showTime(seconds: Long): String = {

    var (sign, h, m, s) = secondsToTime(seconds)
    val time = f"$sign${if (h == 0) "" else h + ":"}$m%02d:$s%02d"
    document.title = if (currentState != Idle) time else "Pomodoro"
    time
  }
}
