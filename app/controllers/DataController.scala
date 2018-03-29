package controllers

import java.util.UUID

import javax.inject.{Inject, Singleton}
import play.api.i18n.I18nSupport
import play.api.mvc._
import repository.PostgresConnection.db

import concurrent.ExecutionContext.Implicits.global

@Singleton
class DataController @Inject()(cc: ControllerComponents)
    extends AbstractController(cc)
    with I18nSupport {

  def pomodoro() = Action.async { implicit request: Request[AnyContent] =>
    val repo = new repository.postgres.PomodoroPqslRepo
    repo
      .startPomodoro(UUID.fromString("117aafab-2ad9-4bc8-ba28-d931d8a6f4a1"))
      .map(updated => Ok(updated.toString))
  }
}
