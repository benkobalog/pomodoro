package model

import java.sql.Timestamp

case class Pomodoro(id: String,
                    start: Timestamp,
                    finish: Option[Timestamp],
                    user_id: String)
