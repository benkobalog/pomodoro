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
                     autoStartBreak: Var[Boolean]) {
    def toUser(user: User): User =
      user
        .copy(pomodoroSeconds = ps.value.toInt * 60)
        .copy(breakSeconds = bs.value.toInt * 60)
        .copy(autoStartBreak = autoStartBreak.value)

    def fromUser(user: User): Unit = {
      ps.value = (user.pomodoroSeconds / 60).toString
      bs.value = (user.breakSeconds / 60).toString
      autoStartBreak.value = user.autoStartBreak
      toggleAutoStart._1.value = if (user.autoStartBreak) "active" else ""
      toggleAutoStart._2.value = if (user.autoStartBreak) "" else "active"
    }
  }

  val userVar = UserVar(Var(""), Var(""), Var(true))

  private val toggleAutoStart = (Var("active"), Var(""))

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
        >Starting break</span>
      </div>
      <div class="btn-group btn-group-toggle" data:data-toggle="buttons">
        <label class={"btn btn-light " + toggleAutoStart._1.bind} id="autoOpt"
               onchange={onChangeRadio(userVar.autoStartBreak.value = true)(_)} >
          <input type="radio" name="autoStartBreak" autocomplete="off" />Automatically</label>

        <label class={"btn btn-light " + toggleAutoStart._2.bind} id="manualOpt"
               onchange={onChangeRadio(userVar.autoStartBreak.value = false)(_)}>
          <input type="radio" name="autoStartBreak" autocomplete="off" />Manually</label>
      </div>
    </div>

    <div class="input-group">
      <div class="input-group-prepend">
        <span class="input-group-text bg-secondary text-light">When time is up</span>
      </div>

    <div class="btn-group btn-group-toggle" data:data-toggle="buttons">
      <label class="btn btn-light">
        <input type="radio" name="options" id="timeup-continue-option" autocomplete="off"/>Warn each
      </label>
    </div>

    <input type="text" class="form-control" id='timeup-continue-length'/>
    <div class="input-group-prepend">
      <span class="input-group-text">minutes</span>
    </div>

    <input type="text" class="form-control" id='timeup-continue-times'/>
      <div class="input-group-prepend">
        <span class="input-group-text">times</span>
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
