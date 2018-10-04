package pomodoro

import org.scalajs.dom.Event

package object logic {
  def secondsToTime(seconds: Long): (String, Long, Long, Long) = {
    val (sign, absSec)= if(seconds < 0) ("-", - seconds) else ("", seconds)
    val hours = absSec / 3600
    val minutes = absSec % 3600 / 60
    val seconds_ = absSec % 3600 % 60
    (sign, hours, minutes, seconds_)
  }

  case class ButtonProps(
      disabled: String,
      text: String,
      color: String,
      onClick: Event => Unit
  )

  case class Controls(left: ButtonProps, right: ButtonProps)
}
