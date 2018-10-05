package pomodoro.logic

import com.thoughtworks.binding.Binding
import org.scalajs.dom.raw.Node
import pomodoro.logic.timer.Timer
import pomodoro.model.wsmessage.{ControlMessage, EndBreak, State}
import pomodoro.model._

class Mediator(settings: Settings, pStats: PomodoroStatistics, td: TokenData) {
  val wsClient = new WebSocketClient(td, this)
  val buttonStates = new ButtonStates(wsClient)
  val timer = new Timer(settings, this)
  val ui = new PomodoroUI(settings, buttonStates, this)

  def controlMessageHandler: ControlMessage => Unit = {
    pStats.getStats // Also update stats (I couldn't find a better place yet)
    ui.controlMessageHandler
  }

  def onFinish(timerLengthSeconds: Int, elapsedSeconds: Int): Unit = {
    def runAfterFinish[A](f: => A) = {
      if (elapsedSeconds >= timerLengthSeconds) {
        f
      }
    }

    currentState match {
      case Idle =>
        println("The State was Idle after a timer run. This shouldn't happen")

      case Running(started) =>
        runAfterFinish(
          controlMessageHandler(State(RunningOvertime(started)))
        )

      case RunningOvertime(started) =>

      case Break(kind, started) =>
        runAfterFinish {
          wsClient.sendMessage(EndBreak)
        }
    }
  }

  def currentState: PomodoroState = ui.currentState

  def createTimer: Int => Unit = timer.createDownTimer

  def clearTimer: () => Unit = timer.clearTimer

  def timerHtml(): Binding[Binding.BindingSeq[Node]] = ui.timerHtml()

}
