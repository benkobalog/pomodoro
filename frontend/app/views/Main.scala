package views
import scalatags.Text
import scalatags.Text.all._

object Main {
  val title_ = "Pomodoro"

  def apply(page: Frag): Text.TypedTag[String] = {
     tag("html")(
      head(
        title := title_,
        link(rel := "stylesheet",
             media := "screen",
             href := "assets/stylesheets/main.css"),
        link(rel := "shortcut icon",
             `type` := "image/png",
             href := "assets/images/favicon.png"),
        link(
          rel := "stylesheet",
          href := "https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css"),
        script(
          src := "https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"),
        script(
          src := "https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js")
      ),
      body(
        script(`type` := "text/javascript",
               src := "assets/javascripts/main.js"),
        div(cls := "container", id := "page-wrapper")(
          div(cls := "collapse", id := "navbarToggleExternalContent")(
            div(cls := "bg-dark p-4")(
              h4(cls := "text-white")("Settings")
            )
          ),
          tag("nav")(cls := "navbar navbar-dark bg-dark")(
            a(cls := "navbar-brand", href := "#")("Pomdoro"),
            button(
              cls := "navbar-toggler",
              `type` := "button",
              data("toggle") := "collapse",
              data("target") := "#navbarToggleExternalContent",
              aria.controls := "navbarToggleExternalContent",
              aria.expanded := "false",
              aria.label := "Toggle navigation"
            )(
              span(cls := "navbar-toggler-icon")()
            )
          ),
          page
        )
      )
    )
  }

}
