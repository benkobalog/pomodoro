package model

sealed trait PomodoroState
case object Idle extends PomodoroState
case object Break extends PomodoroState
case class Running(started: Long) extends PomodoroState
