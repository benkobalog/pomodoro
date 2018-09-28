package pomodoro.logic

import scala.scalajs.js.Date

class SyncTime(startingOffsetMillis: Double = 0.0) {

  private var clockOffsetMillis: Double = startingOffsetMillis

  def getMillis: Double = Date.now() + clockOffsetMillis

  def getOffsetMillis: Double = clockOffsetMillis

  def setTime(serverTimeMillis: Double): Unit =
    clockOffsetMillis = Date.now() - serverTimeMillis
}
