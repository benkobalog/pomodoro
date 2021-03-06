package controllers

import com.typesafe.config.Config
import javax.inject._
import play.api.i18n._
import play.api.mvc._

import scala.concurrent.Future

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(cc: ControllerComponents,
                               authenticatedUserAction: AuthenticatedUserAction,
                               config: Config)
    extends AbstractController(cc)
    with I18nSupport {

  def index() = authenticatedUserAction {
    implicit request: Request[AnyContent] =>
      Ok(views.html.index(config.getString("backend.address")))
  }
}