package pomodoro.logic

import pomodoro.Dependencies
import pomodoro.model.wsmessage.UserRequest
import pomodoro.model.{Idle, MessageType, PomodoroState, Self}
import pomodoro.repository.PomodoroRepo

trait WebSocketTestDeps extends Dependencies {
  override lazy val pomodoroRepo: PomodoroRepo = new PomodoroRepoStub()

  override lazy val pomodoroLogic: PomodoroLogic = new PomodoroLogic(pomodoroRepo) {
    override def stateChanges(
        userId: java.util.UUID,
        message: UserRequest,
        state: PomodoroState
    ): MessageType = Self(Idle)
  }

  override lazy val pStatsRepo = new PomodoroStatsRepoStub()
}
