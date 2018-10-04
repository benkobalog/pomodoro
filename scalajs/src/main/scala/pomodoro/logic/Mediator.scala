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

  def onFinish(timerLengthSeconds: Int): Unit = {
    currentState match {
      case Idle => println("This shouldn't happen")

      case Running(started) =>
        controlMessageHandler(State(RunningOvertime(started)))

      case RunningOvertime(started) =>
        println("Overtime  OnFinish will be invoked every time at the moment")

      case Break(kind, started) =>
        wsClient.sendMessage(EndBreak)
        controlMessageHandler(State(Idle))
    }
  }

  def currentState: PomodoroState = ui.currentState

  def createTimer: Int => Unit = timer.createDownTimer

  def clearTimer: () => Unit = timer.clearTimer

  def timerHtml(): Binding[Binding.BindingSeq[Node]] = ui.timerHtml()

}
