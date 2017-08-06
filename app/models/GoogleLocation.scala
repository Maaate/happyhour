package models

import play.api.libs.json.Json

case class GoogleLocation(placeId: String, primary: String, secondary: String)

object GoogleLocationProtocol {
  implicit lazy val googleLocationFmt = Json.format[GoogleLocation]
}
