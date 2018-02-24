package controllers

import scala.concurrent.ExecutionContext.Implicits.global

import io.swagger.annotations.{Api, ApiOperation}
import javax.inject.Inject
import models.{ServiceType, ServiceTypeGroup}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import repository.{ServiceTypeGroupRepository, ServiceTypeRepository}

@Api(value = "Service Type Api", consumes = "application/json", produces = "application/json")
class ServiceTypeController @Inject()(implicit override val messagesApi: MessagesApi, cc: ControllerComponents, serviceTypeRepository: ServiceTypeRepository, serviceTypeGroupRepository: ServiceTypeGroupRepository) extends AbstractController(cc) with I18nSupport {

  import models.ServiceTypeGroupProtocol._
  import models.ServiceTypeProtocol._

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
