package pomodoro.model

sealed trait MessageType
case class BroadCast(pomodoroState: PomodoroState) extends MessageType
case class Self(pomodoroState: PomodoroState) extends MessageType
case object NoMessage extends MessageType