package pomodoro.logic

import pomodoro.HttpClient
import pomodoro.model.PomodoroStats
import io.circe.generic.auto._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PomodoroStatistics(client: HttpClient) {

  def getStats: Future[Seq[PomodoroStats]] = {
    client
      .get[Seq[PomodoroStats]]("http://localhost:9001/pomodoroStats")
      .map { x =>
        println(x)
        x
      }
  }

}
