package pomodoro

import pomodoro.model.User

import scala.scalajs.js

object Main {
  def main(args: Array[String]): Unit = {
    println("This should work")
    println("Second thing")
  }

  new js.Date()

  User(java.util.UUID.randomUUID(),
       "asd",
       java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()),
       123,
       123)
}
