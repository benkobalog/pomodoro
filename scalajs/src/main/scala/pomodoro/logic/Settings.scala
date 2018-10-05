package pomodoro.logic

import com.softwaremill.sttp.Response
import com.thoughtworks.binding.Binding.{BindingSeq, Var}
import com.thoughtworks.binding.{Binding, dom}
import io.circe.generic.auto._
import org.scalajs.dom.raw._
import pomodoro.HttpClient
import pomodoro.model.User

import scala.concurrent.{ExecutionContext, Future}

class Settings private (httpClient: HttpClient)(implicit ec: ExecutionContext) {

  // I use a private constructor, so this field should always have a value, when this class is used
  private var user: User = null
  def getUser: User = user

  case class UserVar(ps: Var[String],
                     bs: Var[String],
                     continuePomodoro: Var[Boolean],
                     continueBreak: Var[Boolean]) {
    def toUser(user: User): User =
      user
        .copy(pomodoroSeconds = ps.value.toInt * 60)
        .copy(breakSeconds = bs.value.toInt * 60)
        .copy(continuePomodoro = continuePomodoro.value)
        .copy(continueBreak = continueBreak.value)

    def fromUser(user: User): Unit = {
      ps.value = (user.pomodoroSeconds / 60).toString
      bs.value = (user.breakSeconds / 60).toString
      continuePomodoro.value = user.continuePomodoro
      togglePomodoroStop._1.value = if (user.continuePomodoro) "active" else ""
      togglePomodoroStop._2.value = if (user.continuePomodoro) "" else "active"

      toggleBreakStop._1.value = if (user.continueBreak) "active" else ""
      toggleBreakStop._2.value = if (user.continueBreak) "" else "active"
    }
  }

  val userVar = UserVar(Var(""), Var(""), Var(true), Var(false))

  private val togglePomodoroStop = (Var("active"), Var(""))
  private val toggleBreakStop = (Var(""), Var("active"))

  private def fetchUserFromDB(): Future[User] =
    httpClient
      .get[User]("http://localhost:9001/user")
      .map { userInDB =>
        user = userInDB
        userVar.fromUser(userInDB)
        userInDB
      }

  private def saveUser(): Future[Response[String]] = {
    httpClient.put("http://localhost:9001/user")(user)
  }

  var saveButtonEvent: Int => Unit = _

  def setSaveButtonEvent(fnOpt: Option[Int => Unit]): Unit =
    fnOpt match {
      case Some(fn) => saveButtonEvent = fn
      case None     => saveButtonEvent = (_: Any) => ()
    }

  @dom def renderSettings(): Binding[BindingSeq[Node]] = {

    val disabledSave = Var(true)

    @dom lazy val saveButton: Binding[Node] = {
      <button
        type="button"
        class="btn btn-secondary"
        disabled={disabledSave.bind}
        onclick={_: Any =>
          user = userVar.toUser(user)
          saveUser().foreach(_ => disabledSave.value = true)
          saveButtonEvent(user.pomodoroSeconds)
          println(s"Updating user settings: $user")
        }>Save</button>
    }

    def changeHandler(toBind: Var[String]): Event => Unit = { event =>
      event.currentTarget match {
        case input: HTMLInputElement =>
          toBind.value = input.value
          disabledSave.value = userVar.toUser(user) == user
      }
    }

    def onChangeRadio(fn: => Unit)(e: Event): Unit = {
      e.target match {
        case input: HTMLInputElement =>
          fn
          disabledSave.value = userVar.toUser(user) == user
      }
    }

    @dom
    def inputField(toBind: Var[String], label: String): Binding[Node] = {
      <input
        type="text"
        value={toBind.bind}
        class="form-control"
        onchange={changeHandler(toBind)}
        data:aria-label={label}/>
    }

    <div class="input-group">
      <div class="input-group-prepend">
        <span class="input-group-text bg-secondary text-light">Pomodoro length</span>
      </div>
        {inputField(userVar.ps, "Pomodoro length in minutes").bind}
      <div class="input-group-prepend">
        <span class="input-group-text">minutes</span>
      </div>
    </div>

    <div class="input-group">
      <div class="input-group-prepend">
        <span class="input-group-text bg-secondary text-light">Break length</span>
      </div>
        {inputField(userVar.bs, "Break length in minutes").bind}
      <div class="input-group-prepend">
        <span class="input-group-text">minutes</span>
      </div>
    </div>

    <div class="input-group">
      <div class="input-group-prepend">
        <span
          class="input-group-text bg-secondary text-light"
          title="When the pomodoro timer is up, start break automatically or keep going until the pomodoro is manually stopped"
        >Starting Break</span>
      </div>
      <div class="btn-group btn-group-toggle" data:data-toggle="buttons">
        <label class={"btn btn-light " + togglePomodoroStop._1.bind}
               onchange={onChangeRadio(userVar.continuePomodoro.value = true)(_)} >
          <input type="radio" name="continuePomodoro" autocomplete="off" />Manually</label>

        <label class={"btn btn-light " + togglePomodoroStop._2.bind}
               onchange={onChangeRadio(userVar.continuePomodoro.value = false)(_)}>
          <input type="radio" name="continuePomodoro" autocomplete="off" />Automatically</label>
      </div>
    </div>

    <div class="input-group">
      <div class="input-group-prepend">
        <span
          class="input-group-text bg-secondary text-light"
          title="When the break timer is up, stop break timer automatically or continue"
        >Ending Break</span>
      </div>

      <div class="btn-group btn-group-toggle" data:data-toggle="buttons">
        <label class={"btn btn-light " + toggleBreakStop._1.bind}
               onchange={onChangeRadio(userVar.continueBreak.value = true)(_)} >
          <input type="radio" name="continueBreak" autocomplete="off" />Manually</label>

        <label class={"btn btn-light " + toggleBreakStop._2.bind}
               onchange={onChangeRadio(userVar.continueBreak.value = false)(_)}>
          <input type="radio" name="continueBreak" autocomplete="off" />Automatically</label>
      </div>
    </div>

    <div class="input-group">
      <div class="input-group-prepend">
        {saveButton.bind}
        </div>
    </div>

  }
}

object Settings {
  def apply(httpClient: HttpClient)(
      implicit ec: ExecutionContext): Future[Settings] = {
    val settings = new Settings(httpClient)
    settings.fetchUserFromDB().map(_ => settings)
  }
}
