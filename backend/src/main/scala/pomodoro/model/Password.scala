package pomodoro.model

import java.util.UUID

sealed trait Password
case class GeneratedPassword(id: UUID, userId: UUID, password: String)
    extends Password
case class OAuthToken(id: UUID, userId: UUID, token: String) extends Password
