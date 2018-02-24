package forms

import java.util.UUID

import models.User
import play.api.data.Form
import play.api.data.Forms._

object UserForm extends AdditionalMappings {

  def userForm: Form[User] = Form(mapping(
    "id" -> default(uuid, UUID.randomUUID()),
    "email" -> text,
    "token" -> text
  )(User.apply)(User.unapply))
}
