package model

import java.sql.Timestamp

case class PomodoroStats(
    started: Timestamp,
    finished: Timestamp,
    duration: java.time.Duration
)

object PomodoroStats {
  def fromInterval(started: Timestamp, finished: Timestamp) =
    PomodoroStats(started,
             finished,
             java.time.Duration
               .between(started.toLocalDateTime, finished.toLocalDateTime))
}
