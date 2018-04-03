package model

import java.sql.Timestamp

case class Pomodoro(
    started: Timestamp,
    finished: Timestamp,
    duration: java.time.Duration
)

object Pomodoro {
  def fromInterval(started: Timestamp, finished: Timestamp) =
    Pomodoro(started,
             finished,
             java.time.Duration
               .between(finished.toLocalDateTime, started.toLocalDateTime))
}
