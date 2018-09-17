package pomodoro.logic

import pomodoro.model._
import pomodoro.model.wsmessage._

trait PomodoroLogic {

  def stateChanges(message: UserRequest,
                   state: PomodoroState): Option[PomodoroState] =
    (message, state) match {
      case (RequestInit, _) =>
        state.some

      case (StartPomodoro, Idle) =>
        Running(currentTime).some

      case (StartBreak(kind), Running(_)) =>
        Break(kind, currentTime).some

      case (EndPomodoro, Running(_)) =>
        Idle.some

      case (EndBreak, _: Break) =>
        Idle.some

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
