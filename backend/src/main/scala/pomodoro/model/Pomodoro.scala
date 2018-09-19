package pomodoro.model

import java.util.UUID

case class Pomodoro(id: UUID,
                    started: Double,
                    finished: Option[Double] = None,
                    kind: String,
                    usersId: UUID)
