package services

import java.util.UUID
import javax.inject.Inject

import models.Pub
import models.google.GoogleAddressResultComponent
import org.joda.time.DateTime
import play.api.libs.ws.WSClient
import repository.PubRepository

import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class PubService @Inject()( pubRepository: PubRepository, ws: WSClient) {

  import models.google.GoogleAddressResultComponentProtocol._

  val API_KEY = "AIzaSyDsKWNEMGC3x5Px12gJrb0X-vhszDIzwuM"
  val ROOT_ACCOUNT_ID = "9b309e02-5bd2-4f19-a944-fe958aecbdb8"

  def pubFromGoogle(placeId: String): Future[Pub] = {
    val uuid = UUID.randomUUID()
    ws.url("https://maps.googleapis.com/maps/api/place/details/json").withQueryString("placeid" -> placeId, "key" -> API_KEY).get().map {
      response => {
        val res = (response.json \ "result").validate[GoogleAddressResultComponent]
        val pub = Pub(uuid,
          Some(placeId),
          res.get.name,
          res.get.address_components.filterNot(_.types.contains("political")).filterNot(_.types.contains("postal_code")).map(_.long_name).mkString(" "),
          res.get.address_components.filter(_.types.contains("locality")).head.long_name,
          res.get.address_components.filter(_.types.contains("administrative_area_level_1")).head.short_name,
          res.get.address_components.filter(_.types.contains("country")).head.long_name,
          res.get.geometry.location.lng,
          res.get.geometry.location.lat,
          UUID.fromString(ROOT_ACCOUNT_ID),
          res.get.website,
          res.get.international_phone_number,
          res.get.opening_hours.map(_.weekday_text.mkString(",")),
          Some(new DateTime()),
          true,
          BigDecimal.valueOf(0.0),
          Set()
        )
        pub
      }
    }
  }
}
