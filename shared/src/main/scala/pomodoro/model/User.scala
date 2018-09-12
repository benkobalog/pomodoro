package pomodoro.model

case class User(id: java.util.UUID,
                email: String,
                createdAt: Long,
                pomodoroSeconds: Int,
                breakSeconds: Int
                // autoStartBreak: Boolean,
                // stopAtTimeLimit: Boolean,
                // warnPeriodSeconds: Int,
                // warnTimes: Int
               )