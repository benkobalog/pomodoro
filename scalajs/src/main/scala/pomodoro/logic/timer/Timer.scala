package pomodoro.logic.timer

import com.thoughtworks.binding.Binding.Var
import pomodoro.logic.{Mediator, Settings}
import pomodoro.model.{Idle, PomodoroState, RunningOvertime}

import scala.scalajs.js.Date
import scala.scalajs.js.timers.{SetIntervalHandle, clearInterval, setInterval}

class Timer(settings: Settings, mediator: Mediator) {
  private val timeResolution = 1
  private var timer: Option[SetIntervalHandle] = None
  private val timerSeconds: Var[Int] = Var(settings.getUser.pomodoroSeconds)

  def getTimerVar: Var[Int] = timerSeconds

  def createDownTimer(timerLengthSeconds: Int): Unit = {
    timer.foreach(clearInterval)

    var elapsedSeconds = 0

    @inline def eachTime(): Unit = {
      timerSeconds.value -= timeResolution
      elapsedSeconds += timeResolution
    }

    @inline def onZero(): Unit = {
      if (elapsedSeconds >= timerLengthSeconds) {
        mediator.onFinish(timerLengthSeconds)
      }
    }

    timer = Some(setInterval(timeResolution * 1000) {
      eachTime()
      onZero()
    })
  }

  def clearTimer(): Unit = {
    timer.foreach(clearInterval)
  }


  private def runningOverTime(seconds: Int) = {
    RunningOvertime(Date.now() - seconds * 1000)
  }

}
