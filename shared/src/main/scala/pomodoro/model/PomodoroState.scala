package pomodoro.model

sealed trait PomodoroState
case object Idle extends PomodoroState
case class Break(kind: String, started: Long) extends PomodoroState
case class Running(started: Long) extends PomodoroState
