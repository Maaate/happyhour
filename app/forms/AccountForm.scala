package forms

import java.time.LocalDateTime
import java.util.UUID

import models.Account
import play.api.data.Form
import play.api.data.Forms._

object AccountForm {

  val accountForm: Form[Account] = Form(
    mapping(
      "id" -> default(uuid, UUID.randomUUID()),
      "created" -> default(localDateTime, LocalDateTime.now()),
      "name" -> text,
      "username" -> text,
      "password" -> text,
      "firstName" -> text,
      "lastName" -> text,
      "phone" -> text
    )(Account.apply)(Account.unapply))

}
