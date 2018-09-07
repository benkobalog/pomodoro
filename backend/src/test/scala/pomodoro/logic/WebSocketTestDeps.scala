package pomodoro.logic

import pomodoro.Dependencies
import pomodoro.repository.postgres.PomodoroRepoTrait

trait WebSocketTestDeps extends Dependencies {
  override lazy val pomodoroRepo: PomodoroRepoTrait = new PomodoroRepoStub()
}
