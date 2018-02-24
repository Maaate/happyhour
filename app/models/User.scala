package models

import java.util.UUID

import play.api.libs.json.Json

case class User(id: UUID = UUID.randomUUID(),
                email: String,
                token: String)

object UserProtocol {
  implicit lazy val userFmt = Json.format[User]
}