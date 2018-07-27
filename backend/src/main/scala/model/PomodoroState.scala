package model

sealed trait PomodoroState
case object Idle extends PomodoroState
case object Break extends PomodoroState
case class Running(secondsElapsed: Long) extends PomodoroState
