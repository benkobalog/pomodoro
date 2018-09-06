package model.wsmessage

import model.PomodoroState

sealed trait ControlResponse
case class InitializeWith(pomodoroState: PomodoroState) extends ControlResponse
case class SwitchToBreak(kind: String) extends ControlResponse
case object SwitchToPomodoro extends ControlResponse
case object SwitchToIdle extends ControlResponse
