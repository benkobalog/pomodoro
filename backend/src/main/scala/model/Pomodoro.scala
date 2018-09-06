package model

import java.sql.Timestamp
import java.util.UUID

case class Pomodoro(id: java.util.UUID,
                    started: Timestamp,
                    finished: Option[Timestamp] = None,
                    kind: String,
                    usersId: Option[UUID] = None)