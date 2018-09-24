package pomodoro.model

import java.util.UUID

case class Pomodoro(
    id: UUID,
    started: Double,
    finished: Double,
    kind: String,
    usersId: UUID
)
