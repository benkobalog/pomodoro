package repository.dao

import model.{Pomodoro, User}

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

  class PomodoroTemplate(_tableTag: Tag)
      extends profile.api.Table[Pomodoro](_tableTag, "pomodoro") {
    def * =
      (id, started, finished, usersId) <> (Pomodoro.tupled, Pomodoro.unapply)
    def ? =
      (Rep.Some(id), Rep.Some(started), finished, usersId).shaped
        .<>({ r =>
              import r._;
              _1.map(_ => Pomodoro.tupled((_1.get, _2.get, _3, _4)))
            },
            (_: Any) =>
              throw new Exception("Inserting into ? projection not supported."))

    val id = column[java.util.UUID]("id", O.PrimaryKey)
    val started = column[java.sql.Timestamp]("started")
    val finished =
      column[Option[java.sql.Timestamp]]("finished", O.Default(None))
    val usersId = column[Option[java.util.UUID]]("users_id", O.Default(None))

    lazy val usersFk = foreignKey("pomodoro_users_id_fkey", usersId, Users)(
      r => Rep.Some(r.id),
      onUpdate = ForeignKeyAction.NoAction,
      onDelete = ForeignKeyAction.NoAction)
  }
  lazy val Pomodoros = new TableQuery(tag => new PomodoroTemplate(tag))

  class UserTemplate(_tableTag: Tag)
      extends profile.api.Table[User](_tableTag, "users") {
    def * = (id, email, createdAt) <> (User.tupled, User.unapply)
    def ? =
      (Rep.Some(id), Rep.Some(email), Rep.Some(createdAt)).shaped
        .<>({ r =>
              import r._; _1.map(_ => User.tupled((_1.get, _2.get, _3.get)))
            },
            (_: Any) =>
              throw new Exception("Inserting into ? projection not supported."))

    val id = column[java.util.UUID]("id", O.PrimaryKey)
    val email = column[String]("email", O.Length(191, varying = true))
    val createdAt = column[java.sql.Timestamp]("created_at")
    val index1 = index("users_email_key", email, unique = true)
  }
  lazy val Users = new TableQuery(tag => new UserTemplate(tag))
}
