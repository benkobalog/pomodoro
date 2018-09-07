package pomodoro.model

sealed trait PomodoroState
case object Idle extends PomodoroState
case class Break(kind: String, started: java.sql.Timestamp) extends PomodoroState
case class Running(started: java.sql.Timestamp) extends PomodoroState
