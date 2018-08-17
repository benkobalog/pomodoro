package views
import scalatags.Text
import scalatags.Text.all._

object Index {
  def apply = Main.apply(indexFrag)

  private def indexFrag = {
    frag(
      div(cls := "row")(
        div(cls := "col")(
          div(cls := "card")(
            div(cls := "card-body text-center", id := "pomodoro-timer")(
              div(cls := "progress my-3")(
                div(
                  cls := "progress-bar progress-bar-striped progress-bar-animated",
                  role := "progressbar",
                  style := "...",
                  aria.valuenow := "100",
                  aria.valuemin := "0",
                  aria.valuemax := "100"
                )()
              )
            )
          )
        )
      ),
      div(cls := "row")(
        div(cls := "col")(
          button(`type` := "button",
                 cls := "btn btn-outline-success btn-lg btn-block",
                 id := "start-button")(
            "Start"
          )
        ),
        div(cls := "col")(
          button(`type` := "button",
                 cls := "btn btn-outline-danger btn-lg btn-block",
                 id := "stop-button")(
            "Stop"
          )
        )
      ),
      div(id := "accordion")(
        div(cls := "card-mt5")(
          div(cls := "card-header", id := "headingOne")(
            h5(cls := "mb-0 text-center")(
              button(
                cls := "btn",
                data("toggle") := "collapse",
                data("target") := "#collapseOne",
                aria.expanded := "false",
                aria.controls := "collapseOne"
              )("Toggle Last Pomodoros")
            )
          ),
          div(id := "collapseOne",
              cls := "collapse show",
              aria.labelledby := "headingOne",
              data("parent") := "#accordion")(
            div(cls := "card-body")(
              table(cls := "table my-0")(
                thead(
                  tr(
                    th(attr("scope") := "col")("Duration"),
                    th(attr("scope") := "col")("Started"),
                    th(attr("scope") := "col")("Finished")
                  )
                ),
                tbody(id := "lastPomodorosTable")()
              )
            )
          )
        )
      )
    )
  }
}
