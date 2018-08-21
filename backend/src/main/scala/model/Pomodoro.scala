package model

case class Pomodoro(id: java.util.UUID,
                    started: java.sql.Timestamp,
                    finished: Option[java.sql.Timestamp] = None,
                    usersId: Option[java.util.UUID] = None)
