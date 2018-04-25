package models

import java.time.LocalDateTime
import java.util.UUID

//import play.api.db.slick.Config.driver.simple._
import play.api.libs.json.Json

case class Account (id: UUID = UUID.randomUUID(),
                    created: LocalDateTime,
                    name:String,
                    username:String,
                    password:String,
                    firstName:String,
                    lastName:String,
                    phone:String)

object AccountProtocol {
  implicit lazy val accountFmt = Json.format[Account]
}

