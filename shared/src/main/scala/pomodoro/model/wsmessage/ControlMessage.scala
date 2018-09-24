package pomodoro.model.wsmessage

import pomodoro.model.PomodoroState

sealed trait ControlMessage
case class State(pomodoroState: PomodoroState) extends ControlMessage
case class ClockSync(currentTime: Double) extends ControlMessage
