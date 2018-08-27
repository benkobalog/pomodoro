package controllers

import javax.inject._
import model.User
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.mvc._
import play.api.libs.ws._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class LoginController @Inject()(cc: ControllerComponents, ws: WSClient)(
    implicit ec: ExecutionContext)
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

  def processLoginAttempt() = Action.async { implicit req =>
    loginForm
      .bindFromRequest()
      .fold(
        processIncorrectForm,
        processAuthForm
      )
  }

  private def processIncorrectForm(form: Form[User]) = {
    println("Incorrect form: " + form.toString)
    Future.successful(BadRequest("Incorrect form"))
  }

  private def processAuthForm(user: User): Future[Result] = {
    ws.url("http://localhost:9001/authenticate")
      .withAuth(user.email, user.password, WSAuthScheme.BASIC)
      .get()
      .map(
        response =>
          if (response.status == 200) {
            println("creating session")
            Redirect(routes.HomeController.index())
              .withSession("email" -> user.email, "token" -> user.password)
          }
          else Unauthorized("Incorrect email and password combination"))

  }
}
