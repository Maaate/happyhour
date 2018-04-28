package controllers

import scala.concurrent.ExecutionContext.Implicits.global

import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import repository.ReportsRepository

class MetricsController @Inject()(implicit override val messagesApi: MessagesApi, cc: ControllerComponents, reportsRepository: ReportsRepository) extends AbstractController(cc) with I18nSupport {


  import models.reports.UserActivityReportProtocol._

  def userReport() = Action.async {
    implicit request => {
      reportsRepository.recentActivityReport.map {
        report => Ok(Json.toJson(report))
      }
    }
  }

}
