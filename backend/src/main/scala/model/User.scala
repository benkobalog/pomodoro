package model

case class User(id: java.util.UUID,
                email: String,
                createdAt: java.sql.Timestamp,
                pomodoroSeconds: Int,
                breakSeconds: Int
                // autoStartBreak: Boolean,
                // stopAtTimeLimit: Boolean,
                // warnPeriodSeconds: Int,
                // warnTimes: Int
               )
