package models

import play.api.libs.json.Json

case class HealthCheck(minVersion: Int)


object HealthCheckProtocol {
  implicit lazy val healthCheckFmt = Json.format[HealthCheck]

}
