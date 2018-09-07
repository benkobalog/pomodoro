package pomodoro.logic

import pomodoro.Dependencies
import pomodoro.model.PomodoroState
import pomodoro.model.wsmessage.{ControlResponse, SwitchToIdle, UserRequest}
import pomodoro.repository.PomodoroRepoTrait

trait WebSocketTestDeps extends Dependencies {
  override lazy val pomodoroRepo: PomodoroRepoTrait = new PomodoroRepoStub()

  override lazy val pomodoroLogic: PomodoroLogic = new PomodoroLogic {
    override def stateChanges(
                               message: UserRequest,
                               state: PomodoroState): Option[(ControlResponse, PomodoroState)] =
      Some(SwitchToIdle, state)
  }

  override lazy val pStatsRepo = new PomodoroStatsRepoStub()
}