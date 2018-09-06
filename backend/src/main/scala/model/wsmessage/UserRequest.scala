package model.wsmessage

sealed trait UserRequest
case object StartPomodoro extends UserRequest
case class StartBreak(kind: String) extends UserRequest
case object EndPomodoro extends UserRequest
case object EndBreak extends UserRequest
