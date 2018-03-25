package controllers

import scala.concurrent.Future

import javax.inject.Inject
import models.HealthCheck
import play.api.Configuration
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}

class SystemController @Inject()(implicit override val messagesApi: MessagesApi, cc: ControllerComponents, config: Configuration) extends AbstractController(cc) with I18nSupport {

  import models.HealthCheckProtocol._


  def healthCheck() = Action.async {
    implicit request => {
      Future.successful(Ok(Json.toJson(HealthCheck(config.get[Int]("external.appVersion.minimum")))))
    }
  }

}
