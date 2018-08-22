package models

import java.time.LocalDateTime
import java.util.UUID

import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._

case class User(id: UUID = UUID.randomUUID(),
                created: LocalDateTime,
                lastLoggedIn: LocalDateTime,
                googleUid: String,
                email: String,
                name: String)

object UserProtocol {
  implicit lazy val userFmt = Json.format[User]
}