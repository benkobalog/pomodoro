package pomodoro.model

import java.util.UUID

case class RunningPomodoro(
    id: UUID,
    started: Double,
    kind: String,
    usersId: UUID
) {
  def toPomodoro(finished: Double): Pomodoro =
    Pomodoro(id, started, finished, kind, usersId)
}
