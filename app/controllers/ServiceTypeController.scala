package controllers

import javax.inject.Inject

import io.swagger.annotations.{ApiOperation, Api}
import models.{ServiceTypeGroup, ServiceType}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import repository.{ServiceTypeGroupRepository, ServiceTypeRepository}

import scala.concurrent.ExecutionContext.Implicits.global

@Api(value = "Service Type Api", consumes = "application/json", produces = "application/json")
class ServiceTypeController @Inject()(implicit val messagesApi: MessagesApi, serviceTypeRepository: ServiceTypeRepository, serviceTypeGroupRepository: ServiceTypeGroupRepository) extends Controller with I18nSupport {

  import models.ServiceTypeProtocol._
  import models.ServiceTypeGroupProtocol._

  @ApiOperation(value = "View the service types", response = classOf[ServiceType], responseContainer = "List")
  def listServiceTypes() =
    Action.async { implicit request =>
      serviceTypeRepository.list.map {
        serviceTypes =>
          render {
            case _ => Ok(Json.toJson(serviceTypes))
          }
      }
    }

  @ApiOperation(value = "View the service types groups", response = classOf[ServiceTypeGroup], responseContainer = "List")
  def listServiceTypeGroups() =
    Action.async { implicit request =>
      serviceTypeGroupRepository.list.map {
        serviceTypeGroups =>
          render {
            case _ => Ok(Json.toJson(serviceTypeGroups))
          }
      }
    }
}
