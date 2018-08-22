package controllers

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global

import actions.{AuthorisedAction, PossibleAuthorisedAction}
import forms.PromotionForm
import io.swagger.annotations._
import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import repository.{PromotionRepository, PubRepository, ServiceTypeRepository}
import services.PromotionService

@Api(value = "Promotion Api", consumes = "application/json", produces = "application/json")
class PromotionController @Inject()(implicit override val messagesApi: MessagesApi,
                                    cc: ControllerComponents,
                                    promotionRepository: PromotionRepository,
                                    pubRepository: PubRepository,
                                    serviceTypeRepository: ServiceTypeRepository,
                                    promotionService: PromotionService,
                                    auth: AuthorisedAction,
                                    possibleAuth: PossibleAuthorisedAction) extends AbstractController(cc) with I18nSupport {


  import models.PromotionProtocol._

  @ApiOperation(value= "Create Promotion Page Backend", hidden = true)
  def createPromotion() =
    Action.async { implicit request => {
      for {
        serviceTypes <- serviceTypeRepository.list
      } yield render {
        case Accepts.Html() => Ok(views.html.promotion.editPromotion(PromotionForm.promotionForm.bindFromRequest().discardingErrors, serviceTypes))
        case _ => UnsupportedMediaType("")
      }
    }
    }

  @ApiOperation(value= "Edit Promotion Page Backend", hidden = true)
  def editPromotion(id: UUID) = Action.async { implicit request =>
    for {
      promotion <- promotionRepository.get(id)
      serviceTypes <- serviceTypeRepository.list
    } yield render {
      case Accepts.Html() => Ok(views.html.promotion.editPromotion(PromotionForm.promotionForm.fill(promotion), serviceTypes))
      case _ => UnsupportedMediaType("")

    }
  }

  @ApiOperation(value = "Save Promotion", notes = "This needs firebase authentication to work and I haven't set that up so it won't work, just look and enjoy for the time being")
  @ApiResponses(Array(new ApiResponse(code = 400, message = "Invalid input")))
  @ApiImplicitParams(Array(new ApiImplicitParam(value = "The Form Body", required = true, dataType = "forms.PromotionFormData", paramType = "body")))
  def savePromotion() =
    auth.async {
      implicit request =>
        PromotionForm.promotionForm.bindFromRequest().fold({
          formWithErrors => for {
            serviceTypes <- serviceTypeRepository.list
          } yield render {
            case Accepts.Json() => BadRequest(Json.toJson(formWithErrors.errors.map(e => s"${e.key}:${e.message}")))
            //case Accepts.Html() => BadRequest(views.html.promotion.editPromotion(formWithErrors, serviceTypes))
            case _ => UnsupportedMediaType("errors")
          }
        }, {
          promotion => for {
            - <- promotionService.save(promotion, request.user)
            pub <- pubRepository.get(promotion.pubId)
          } yield render {
            case Accepts.Json() => Ok("")
            //case Accepts.Html() => Ok(views.html.pub.viewPub(pub))
            case _ => UnsupportedMediaType("Unsupported Media type")
          }
        }

        )
    }

  @ApiOperation(value = "Find Similar Promotion")
  @ApiResponses(Array(new ApiResponse(code = 400, message = "Invalid input")))
  @ApiImplicitParams(Array(new ApiImplicitParam(value = "The Form Body", required = true, dataType = "forms.PromotionFormData", paramType = "body")))
  def similarPromotion() =
    Action.async {
      implicit request =>
        PromotionForm.promotionForm.bindFromRequest().fold({
          formWithErrors => for {
            serviceTypes <- serviceTypeRepository.list
          } yield render {
            case Accepts.Json() => BadRequest(Json.toJson(formWithErrors.errors.map(e => s"${e.key}:${e.message}")))
            //case Accepts.Html() => BadRequest(views.html.promotion.editPromotion(formWithErrors, serviceTypes))
            case _ => UnsupportedMediaType("errors")
          }
        }, {
          promotion => for {
            promotions <- promotionRepository.getSimilar(promotion)
          } yield render {
            case Accepts.Json() => Ok(Json.toJson(promotions))
            //case Accepts.Html() => Ok(views.html.pub.viewPub(pub))
            case _ => UnsupportedMediaType("Unsupported Media type")
          }
        }

        )
    }



  @ApiOperation(value = "Disable the Promotion", response = classOf[String], consumes = "*")
  def disablePromotion(@ApiParam(value = "ID of the promotion to disable", example = "92f62d66-e336-430d-9441-d49e7dce2acd") id: UUID) = Action.async { implicit request =>
    for {
      - <- promotionRepository.setEnabled(id, false)
      promotion <- promotionRepository.get(id)
      pub <- pubRepository.get(promotion.pubId)
    } yield render {
      case Accepts.Json() => Ok("")
      case Accepts.Html() => Ok(views.html.pub.viewPub(pub))
      case _ => UnsupportedMediaType("Unsupported Media type")
    }
  }

  @ApiOperation(value = "Enable the Promotion", response = classOf[String], consumes = "*")
  def enablePromotion(@ApiParam(value = "ID of the promotion to enable", example = "92f62d66-e336-430d-9441-d49e7dce2acd") id: UUID) = Action.async { implicit request =>
    for {
      - <- promotionRepository.setEnabled(id, true)
      promotion <- promotionRepository.get(id)
      pub <- pubRepository.get(promotion.pubId)
    } yield render {
      case Accepts.Json() => Ok("")
      case Accepts.Html() => Ok(views.html.pub.viewPub(pub))
      case _ => UnsupportedMediaType("Unsupported Media type")
    }
  }

}
