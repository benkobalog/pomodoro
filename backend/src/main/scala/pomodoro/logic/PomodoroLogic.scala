package pomodoro.logic

import pomodoro.model._
import pomodoro.model.wsmessage._

trait PomodoroLogic {

  def stateChanges(
      message: UserRequest,
      state: PomodoroState): Option[(ControlResponse, PomodoroState)] =
    (message, state) match {
      case (RequestInit, _) =>
        (InitializeWith(state), state).some

      case (StartPomodoro, Idle) =>
        (SwitchToPomodoro, Running(currentTime)).some

      case (StartBreak(kind), Running(_)) =>
        (SwitchToBreak(kind), Break(kind, currentTime)).some

      case (EndPomodoro, Running(_)) =>
        (SwitchToIdle, Idle).some

      case (EndBreak, _: Break) =>
        (SwitchToIdle, Idle).some

      case _ => None
    }

  private def currentTime: Long =
    System.currentTimeMillis()

  private implicit class SomeOps[A](a: A) {
    def some: Option[A] = Some(a)
  }
}

object PomodoroLogic extends PomodoroLogic {
  def apply: PomodoroLogic = new PomodoroLogic() {}
}
