package pomodoro.logic

import com.thoughtworks.binding.Binding.{BindingSeq, Var, Vars}
import com.thoughtworks.binding.{Binding, dom}
import io.circe.generic.auto._
import org.scalajs.dom.Event
import org.scalajs.dom.html.Button
import org.scalajs.dom.raw.Node
import pomodoro.HttpClient
import pomodoro.model.PomodoroStats
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class PomodoroStatistics(client: HttpClient) {

  def getStats: Future[Seq[PomodoroStats]] = {
    client
      .get[Seq[PomodoroStats]]("http://localhost:9001/pomodoroStats")
      .map { ps =>
        data.value
        data.value ++= ps
        ps
      }
  }

  private val data: Vars[PomodoroStats] = Vars.empty


  @dom
  def button1: Binding[Button] = {
    <button onclick={e: Event =>
      data.value
      data.value ++= Seq(PomodoroStats(1, 2))}
    >Add
    </button>
  }
  @dom
  def stats(): Binding[Node] = {
    <div>
    {button1.bind}
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
                {stat.started.toString}
              </td>
              <td>
                {stat.finished.toString}
              </td>
            </tr>
          }
        }
      </tbody>
    </table>
    </div>
  }
}
