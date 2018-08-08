package controllers

import javax.inject._
import model.User
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.mvc._

@Singleton
class LoginController @Inject()(cc: ControllerComponents)
    extends AbstractController(cc)
    with I18nSupport {

  private val loginForm = Form(
    mapping(
      "email" -> nonEmptyText,
      "password" -> nonEmptyText(minLength = 4)
    )(User.apply)(User.unapply)
  )

  def showLoginForm() = Action { implicit request: Request[AnyContent] =>
    Ok(
      views.html.login(loginForm, routes.LoginController.processLoginAttempt()))
  }

  def processLoginAttempt() = Action { implicit req =>
    loginForm
      .bindFromRequest()
      .fold(
        x => BadRequest(x.toString),
        user =>
          Redirect(routes.HomeController.index())
            .withSession("email" -> user.email, "token" -> user.password)
      )
  }
}
