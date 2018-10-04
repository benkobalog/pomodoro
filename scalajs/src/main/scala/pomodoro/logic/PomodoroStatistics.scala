package pomodoro.logic

import com.thoughtworks.binding.Binding.Vars
import com.thoughtworks.binding.{Binding, dom}
import io.circe.generic.auto._
import org.scalajs.dom.raw.Node
import pomodoro.HttpClient
import pomodoro.model.PomodoroStats

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js.Date

class PomodoroStatistics(client: HttpClient) {

  def getStats: Future[Seq[PomodoroStats]] = {
    client
      .get[Seq[PomodoroStats]]("http://localhost:9001/pomodoroStats")
      .map { ps =>
        data.value.clear()
        data.value ++= ps
        ps
      }
  }

  private val data: Vars[PomodoroStats] = Vars.empty

  @dom
  def stats(): Binding[Node] = {
    <div>
      <table class="table my-0">
        <thead>
          <tr>
            <th>Duration</th>
            <th>Started</th>
            <th>Finished</th>
          </tr>
        </thead>
        <tbody id="lastPomodorosTable">
          {
            for(stat <- data) yield {
              <tr>
                <td>
                  {displayStats(stat)}
                </td>
                <td>
                  {epochToTimeString(stat.started)}
                </td>
                <td>
                  {epochToTimeString(stat.finished)}
                </td>
              </tr>
            }
          }
        </tbody>
      </table>
    </div>
  }

  private def epochToTimeString(millis: Double): String = {
    def englishTime(value: Long, unit: String) =
      value + " " + (if (value == 1) unit else unit + "s") + " ago"

    val (_, h, m, s) = secondsToTime((Date.now() - millis).toLong / 1000)
    if (h != 0) englishTime(h, "hour")
    else if (m != 0) englishTime(m, "minute")
    else englishTime(s, "second")
  }

  private def displayStats(pomodoroStats: PomodoroStats): String = {
    val (_, h, m, s) =
      secondsToTime(
        (pomodoroStats.finished - pomodoroStats.started).toLong / 1000)
    f"${if (h == 0) "" else h + "h "}$m%02dm $s%02ds"
  }
}
