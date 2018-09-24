package pomodoro.logic

import com.softwaremill.sttp.Response
import com.thoughtworks.binding.Binding.{BindingSeq, Var}
import com.thoughtworks.binding.{Binding, dom}
import io.circe.generic.auto._
import org.scalajs.dom.raw.{Event, HTMLInputElement, Node}
import pomodoro.HttpClient
import pomodoro.model.User

import scala.concurrent.{ExecutionContext, Future}

class Settings private (httpClient: HttpClient)(implicit ec: ExecutionContext) {

  // I use a private constructor, so this field should always have a value, when this class is used
  private var user: User = null
  def getUser: User = user

  case class UserVar(ps: Var[String], bs: Var[String]) {
    def toUser(user: User): User =
      user
        .copy(pomodoroSeconds = ps.value.toInt * 60)
        .copy(breakSeconds = bs.value.toInt * 60)

    def fromUser(user: User) = {
      ps.value = (user.pomodoroSeconds / 60).toString
      bs.value = (user.breakSeconds / 60).toString
    }
  }

  val userVar = UserVar(Var(""), Var(""))

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

  def setSaveButtonEvent(fn: Int => Unit): Unit = saveButtonEvent = fn

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

    def changeHandler(toBind: Var[String]): Event => Unit = { event: Event =>
      event.currentTarget match {
        case input: HTMLInputElement =>
          toBind.value = input.value
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
        <span class="input-group-text bg-secondary text-light">Starting break</span>
      </div>
      <div class="btn-group btn-group-toggle" data:data-toggle="buttons">
        <label class="btn btn-light active">
          <input type="radio" name="options" id="break-auto-option" autocomplete="off" data:checked=""/>Automatically
        </label>
        <label class="btn btn-light">
          <input type="radio" name="options" id="break-manual-option" autocomplete="off" data:checked="checked"/>Manually
        </label>
      </div>
    </div>

    <div class="input-group">
      <div class="input-group-prepend">
        <span class="input-group-text bg-secondary text-light">When time is up</span>
      </div>

    <div class="btn-group btn-group-toggle" data:data-toggle="buttons">
      <label class="btn btn-light active">
        <input type="radio" name="options" id="timeup-stop-option" autocomplete="off"/>Stop timer
      </label>
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
