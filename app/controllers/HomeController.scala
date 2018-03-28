package controllers

import javax.inject._
import play.api._
import play.api.i18n._
import play.api.mvc._

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(cc: ControllerComponents)
    extends AbstractController(cc)  with I18nSupport {

  def userForm() = Action { implicit req =>
    UserForm.form.bindFromRequest().fold(
      x => BadRequest(x.toString),
      user => Ok(user.toString)
    )
  }
  
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index(UserForm.form))
  }
}
