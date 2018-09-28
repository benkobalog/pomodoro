package pomodoro

import org.scalajs.dom.Event

package object logic {
  def secondsToTime(seconds: Long): (Long, Long, Long) = {
    val hours = seconds / 3600
    val minutes = seconds % 3600 / 60
    val seconds_ = seconds % 3600 % 60
    (hours, minutes, seconds_)
  }

  case class ButtonProps(
      disabled: String,
      text: String,
      color: String,
      onClick: Event => Unit
  )

  case class Controls(left: ButtonProps, right: ButtonProps)
}
