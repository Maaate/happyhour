package controllers

import java.util.UUID
import javax.inject.Inject

import forms.PubForm
import io.swagger.annotations.{Api, ApiOperation, ApiParam}
import models.PubResult
import org.joda.time.{DateTime, LocalTime}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, Controller}
import repository.PubRepository
import repository.helpers.{FullPubSearchQuery, Location, SimpleQuery}
import services.PubService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Api(value = "Pub Api", consumes = "application/json", produces = "application/json")
class PubController @Inject()(implicit val messagesApi: MessagesApi, pubRepository: PubRepository, pubService: PubService, ws: WSClient) extends Controller with I18nSupport {

  import models.PubProtocol._

  val SEARCH_DIFFERENCE_IN_METRES = 10000
  val API_KEY = "AIzaSyDsKWNEMGC3x5Px12gJrb0X-vhszDIzwuM"
  val ROOT_ACCOUNT_ID = "9b309e02-5bd2-4f19-a944-fe958aecbdb8"

  @ApiOperation(value = "View Pub Page Backend", hidden = true)
  def viewPub(id: UUID) = {
    Action.async { implicit request =>
      for {
        pub <- pubRepository.get(id)
      } yield render {
        case Accepts.Html() => Ok(views.html.pub.viewPub(pub))
        case _ => UnsupportedMediaType("")
      }
    }
  }

  @ApiOperation(value = "Create Pub Page Backend", hidden = true)
  def createPub =
    Action { implicit request => Ok(views.html.pub.editPub(PubForm.pubForm))
    }

  @ApiOperation(value = "Edit Pub Page Backend", hidden = true)
  def editPub(id: UUID) = Action.async { implicit request =>
    for {
      pub <- pubRepository.get(id)
    } yield render {
      case Accepts.Html() => Ok(views.html.pub.editPub(PubForm.pubForm.fill(pub)))
      case _ => UnsupportedMediaType("")
    }
  }

  @ApiOperation(value = "List Pubs Page Backend", hidden = true)
  def listPubs(term: Option[String] = None, page: Option[Int] = None, items: Option[Int] = None) =
    Action.async { implicit request =>
      pubRepository.list(SimpleQuery()).map {
        pubs => render {
          case Accepts.Html() => Ok(views.html.pub.pubs(pubs))
          case _ => UnsupportedMediaType("")
        }
      }
    }

  @ApiOperation(value = "Save Pub Backend", hidden = true)
  def savePub() =
    Action.async {
      implicit request =>
        PubForm.pubForm.bindFromRequest().fold({
          formWithErrors => Future.successful(render {
            case Accepts.Html() => BadRequest(views.html.pub.editPub(formWithErrors))
            case _ => UnsupportedMediaType("")
          })
        }, {
          pub => for {
            - <- pubRepository.save(pub)
            updatedPub <- pubRepository.get(pub.id)
          } yield render {
            case Accepts.Html() => Ok(views.html.pub.viewPub(updatedPub))
            case _ => UnsupportedMediaType("")
          }
        })
    }


  @ApiOperation(value = "Retrieve Pub from Google if doesn't exist", response = classOf[PubResult])
  def findByGoogle(@ApiParam(value = "Google Id", example = "ChIJZU5G6zyuEmsRMbD0fFwItf8") placeId: String) = Action.async {
    implicit request =>
      for {
        maybePub <- pubRepository.search(FullPubSearchQuery(googleId = Some(placeId))).map {
          pubs => {
            pubs.headOption
          }
        }
        pub <- maybePub match {
          case Some(p) => Future.successful(p)
          case _ => pubService.pubFromGoogle(placeId)
        }
        _ <- pubRepository.save(pub.copy(googleId = Some(placeId)))
      } yield Ok(Json.toJson(PubResult(pub)))
  }

  @ApiOperation(value = "View the pub", response = classOf[PubResult])
  def viewJsonPub(@ApiParam(value = "ID of the pub to fetch", example = "eddcd6cd-b8c3-43a9-bef6-9fad92d378ed") id: UUID) = {
    Action.async { implicit request =>
      for {
        pub <- pubRepository.get(id)
      } yield Ok((Json.toJson(PubResult(pub))))
    }
  }

  @ApiOperation(value = "Search nearby pub", response = classOf[PubResult], responseContainer = "List")
  def fullSearch(@ApiParam(value = "Your Latitude", example = "-140") lat: Double,
                 @ApiParam(value = "Your Longtitude", example = "-60") long: Double,
                 @ApiParam(value = "Radius Search") distanceInMetres: Double = SEARCH_DIFFERENCE_IN_METRES,
                 @ApiParam(value = "24 hour Time Format (HH:mm)", example = "18:30") currentTime: LocalTime,
                 @ApiParam(value = "Monday = 1 ... Sunday = 7", example = "5") dayOfWeek: Option[Int] = None,
                 @ApiParam(value = "Ids of of the service types, separated by comma", example = "1,2,3") serviceTypesIds: List[Long] = List(),
                 @ApiParam(value = "Ids of of the service types groups, separated by comma", example = "1") serviceTypeGroupIds: List[Long] = List(),
                 @ApiParam(value = "Only show currently running promotions") current: Boolean = false,
                 @ApiParam(value = "Paged Index") page: Int = 1,
                 @ApiParam(value = "Number of results to be returned") items: Int = 20
                ) =
    Action.async { implicit request =>
      val daytext = dayOfWeek match {
        case Some(day) => new DateTime().withDayOfWeek(day).dayOfWeek().getAsText
        case _ => DateTime.now().dayOfWeek().getAsText
      }
      val x = serviceTypeGroupIds
      val y = serviceTypesIds
      pubRepository.search(FullPubSearchQuery(Location(lat, long, distanceInMetres), Some(currentTime), Some(daytext), current = current)).map {
        pubs => Ok(Json.toJson(pubs.map(PubResult(_))))
      }
    }

  def test(ids: List[String]) = {
    Action { implicit request =>
      Ok(ids.last)
    }
  }

  def test2(ids: List[Long]) = {
    Action { implicit request =>
      Ok(ids.last.toString)
    }
  }

}