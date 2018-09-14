package pomodoro.model

sealed trait PomodoroState
case object Idle extends PomodoroState
case class Break(kind: String, started: Double) extends PomodoroState
case class Running(started: Double) extends PomodoroState
