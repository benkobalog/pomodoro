package pomodoro.model

import java.util.UUID

case class Pomodoro(id: java.util.UUID,
                    started: Double,
                    finished: Option[Double] = None,
                    kind: String,
                    usersId: Option[UUID] = None)