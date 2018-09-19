package pomodoro.logic

import java.util.UUID

import pomodoro.model._
import pomodoro.model.wsmessage._
import pomodoro.repository.PomodoroStatsRepo

class PomodoroLogic(pomodoroStatsRepo: PomodoroStatsRepo) {

  def stateChanges(userId: UUID,
                   message: UserRequest,
                   state: PomodoroState): Option[PomodoroState] =
    (message, state) match {
      case (RequestInit, _) =>
        state.some

      case (StartPomodoro, Idle) =>
        Running(currentTime).some

      case (StartBreak(kind), Running(started)) =>
        addPomodoro(started, userId)
        Break(kind, currentTime).some

      case (EndPomodoro, Running(started)) =>
        addPomodoro(started, userId)
        Idle.some

      case (EndBreak, _: Break) =>
        Idle.some

      case _ => None
    }

  private def addPomodoro(started: Double, userId: UUID) =
    pomodoroStatsRepo.add(started, System.currentTimeMillis().toDouble, userId)

  private def currentTime: Long =
    System.currentTimeMillis()

  private implicit class SomeOps[A](a: A) {
    def some: Option[A] = Some(a)
  }
}
