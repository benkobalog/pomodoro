package controllers

import java.util.UUID

import javax.inject.{Inject, Singleton}
import play.api.i18n.I18nSupport
import play.api.mvc._
import repository.PostgresConnection.db
import io.circe.generic.auto._
import io.circe.syntax._
import utils.implicits.Circe._
import utils.implicits.Play._
import concurrent.ExecutionContext.Implicits.global

@Singleton
class DataController @Inject()(cc: ControllerComponents)
    extends AbstractController(cc)
    with I18nSupport {

  private val repo = new repository.postgres.PomodoroPqslRepo

  private val uuid: UUID =
    UUID.fromString("7c0325b7-7ead-4c24-abfe-5a7b4e0fc60f")

  def pomodoroStart() = Action.async { implicit request: Request[AnyContent] =>
    repo
      .startPomodoro(uuid)
      .map(updated => Ok(updated.toString))
  }

  def pomodoroFinish() = Action.async { implicit request: Request[AnyContent] =>
    repo
      .finishPomodoro(uuid)
      .map(updated => Ok(updated.toString))
  }

  def pomodoroGet() = Action.async { implicit request: Request[AnyContent] =>
    repo
      .get(uuid)
      .map(x => Ok(x.asJson))
  }
}
