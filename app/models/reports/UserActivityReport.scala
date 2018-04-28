package models.reports

import java.time.LocalDateTime

import play.api.libs.json.Json

case class UseActivityReport(userName: String, time: LocalDateTime, pubName: String, description: String)

object UserActivityReportProtocol {
  implicit lazy val userActivityReportFmt = Json.format[UseActivityReport]
  //implicit lazy val userActivityReportsFmt = Json.format[List[UseActivityReport]]
}
