package pomodoro

package object logic {
  def secondsToTime(seconds: Long): (Long, Long, Long) = {
    val hours = seconds / 3600
    val minutes = seconds % 3600 / 60
    val seconds_ = seconds % 3600 % 60
    (hours, minutes, seconds_)
  }
}
