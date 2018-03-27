package controllers

object UserForm {
  import play.api.data.Forms._
  import play.api.data.Form


  case class User(name: String, password: String)

  val form = Form(
    mapping(
      "name" -> nonEmptyText,
      "password" -> nonEmptyText(minLength = 10)
    )(User.apply)(User.unapply)
  )

}
