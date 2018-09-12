package pomodoro.model

import java.util.UUID

case class Pomodoro(id: java.util.UUID,
                    started: Long,
                    finished: Option[Long] = None,
                    kind: String,
                    usersId: Option[UUID] = None)