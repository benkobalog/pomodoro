package pomodoro.logic

import pomodoro.Dependencies
import pomodoro.repository.PomodoroRepo

trait WebSocketTestDeps extends Dependencies {
  override lazy val pomodoroRepo: PomodoroRepo = new PomodoroRepoStub()

  protected val defaultTime = 0.0
  override lazy val pomodoroLogic: PomodoroLogic = new PomodoroLogic(pomodoroRepo, defaultTime)

  override lazy val pStatsRepo = new PomodoroStatsRepoStub()
}
