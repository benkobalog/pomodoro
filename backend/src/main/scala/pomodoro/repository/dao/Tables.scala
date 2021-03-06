package pomodoro.repository.dao

import pomodoro.model._
import slick.lifted.ProvenShape

object Tables extends {
  val profile = slick.jdbc.PostgresProfile
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Pomodoros.schema ++ Users.schema

  // ==================== Pomodoro ====================
  class PomodoroTemplate(_tableTag: Tag)
      extends profile.api.Table[Pomodoro](_tableTag, "pomodoro") {
    def * =
      (id, started, finished, kind, usersId) <> (Pomodoro.tupled, Pomodoro.unapply)

    val id = column[java.util.UUID]("id", O.PrimaryKey)
    val started = column[Double]("started")
    val finished = column[Double]("finished")
    val kind = column[String]("kind")
    val usersId = column[java.util.UUID]("users_id")

    lazy val usersFk = foreignKey("pomodoro_users_id_fkey", usersId, Users)(
      r => r.id,
      onUpdate = ForeignKeyAction.NoAction,
      onDelete = ForeignKeyAction.NoAction)
  }
  lazy val Pomodoros = new TableQuery(tag => new PomodoroTemplate(tag))

  // ================= RunningPomodoro =================
  class RunningPomodoroTemplate(_tableTag: Tag)
      extends profile.api.Table[RunningPomodoro](_tableTag, "running_pomodoro") {
    def * =
      (id, started, kind, usersId) <> (RunningPomodoro.tupled, RunningPomodoro.unapply)

    val id = column[java.util.UUID]("id", O.PrimaryKey)
    val started = column[Double]("started")
    val kind = column[String]("kind")
    val usersId = column[java.util.UUID]("users_id")

    lazy val usersFk =
      foreignKey("running_pomodoro_users_id_fkey", usersId, Users)(
        r => r.id,
        onUpdate = ForeignKeyAction.NoAction,
        onDelete = ForeignKeyAction.NoAction)
  }
  lazy val RunningPomodoros = new TableQuery(
    tag => new RunningPomodoroTemplate(tag))

  // ====================== User ======================
  class UserTemplate(_tableTag: Tag)
      extends profile.api.Table[User](_tableTag, "users") {
    def * =
      (id,
       email,
       createdAt,
       pomodoroSeconds,
       breakSeconds,
       continue_pomodoro,
       continue_break) <> (User.tupled, User.unapply)

    def ? =
      (Rep.Some(id),
       Rep.Some(email),
       Rep.Some(createdAt),
       Rep.Some(pomodoroSeconds),
       Rep.Some(breakSeconds),
       Rep.Some(continue_pomodoro),
       Rep.Some(continue_break)).shaped
        .<>(
          { r =>
            import r._
            _1.map(
              _ =>
                User.tupled(
                  (_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get)))
          },
          (_: Any) =>
            throw new Exception("Inserting into ? projection not supported.")
        )

    val id = column[java.util.UUID]("id", O.PrimaryKey)
    val email = column[String]("email", O.Length(191, varying = true))
    val createdAt = column[Double]("created_at")
    val pomodoroSeconds = column[Int]("pomodoro_seconds")
    val breakSeconds = column[Int]("break_seconds")
    val continue_pomodoro = column[Boolean]("continue_pomodoro")
    val continue_break = column[Boolean]("continue_break")
    val index1 = index("users_email_key", email, unique = true)
  }
  lazy val Users = new TableQuery(tag => new UserTemplate(tag))

  // ================ GeneratedPassword ================
  class GeneratedPasswordTemplate(_tableTag: Tag)
      extends profile.api.Table[GeneratedPassword](_tableTag,
                                                   "generated_password") {
    override def * =
      (id, usersId, password) <> (GeneratedPassword.tupled, GeneratedPassword.unapply)

    val id = column[java.util.UUID]("id", O.PrimaryKey)
    val usersId = column[java.util.UUID]("users_id")
    val password = column[String]("password")

    lazy val usersFk =
      foreignKey("generated_password_users_id_fkey", usersId, Users)(
        r => r.id,
        onUpdate = ForeignKeyAction.NoAction,
        onDelete = ForeignKeyAction.NoAction)
  }

  lazy val GeneratedPasswords = new TableQuery(
    tag => new GeneratedPasswordTemplate(tag))

  // =================== OauthToken ===================
  class OAuthTokenTemplate(_tableTag: Tag)
      extends profile.api.Table[OAuthToken](_tableTag, "oauth_token") {
    override def * : ProvenShape[OAuthToken] =
      (id, usersId, token) <> (OAuthToken.tupled, OAuthToken.unapply)

    val id = column[java.util.UUID]("id", O.PrimaryKey)
    val usersId = column[java.util.UUID]("users_id")
    val token = column[String]("token")

    lazy val usersFk =
      foreignKey("oauth_token_users_id_fkey", usersId, Users)(
        r => r.id,
        onUpdate = ForeignKeyAction.NoAction,
        onDelete = ForeignKeyAction.NoAction)
  }

  lazy val OAuthTokens = new TableQuery(tag => new OAuthTokenTemplate(tag))
}
