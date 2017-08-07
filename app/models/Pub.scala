package models

import java.util.UUID

import io.swagger.annotations.ApiModelProperty
import org.joda.time.DateTime
import play.api.libs.json.Json
import models.PromotionProtocol._


case class Pub(id: UUID,
               googleId: Option[String],
               name: String,
               address: String,
               addressSuburb: String,
               addressState: String,
               addressCountry: String,
               longitude: BigDecimal,
               latitude: BigDecimal,
               accountId: UUID,
               website: Option[String],
               phoneNumber: Option[String],
               hoursOpenString: Option[String],
               lastUpdatedByGoogle: Option[DateTime],
               enabled: Boolean,
               //distanceInMetres: BigDecimal,
               promotions: Set[Promotion]) {

  def withPromotions(proms: Set[Promotion]) = {
    val updatedPromotions = (promotions ++ proms).foldLeft(List[Promotion]()) {
      case (existing: List[Promotion], p: Promotion) => {
        existing.filterNot(_.id == p.id) :+ existing.find(_.id == p.id).getOrElse(p).withServiceTypes(p.serviceTypes)
      }
    }
    this.copy(promotions = updatedPromotions.toSet)
  }
}

case class PubResult(@ApiModelProperty(required = true) id: UUID,
                     @ApiModelProperty(required = true) name: String,
                     @ApiModelProperty(required = true) address: String,
                     @ApiModelProperty(required = true) addressSuburb: String,
                     @ApiModelProperty(required = true) addressState: String,
                     @ApiModelProperty(required = true) addressCountry: String,
                     phoneNumber: Option[String],
                     website: Option[String],
                     hours: Option[String],
                     @ApiModelProperty(required = true) longitude: BigDecimal,
                     @ApiModelProperty(required = true) latitude: BigDecimal,
                     //@ApiModelProperty(required = true) distance: BigDecimal,
                     @ApiModelProperty(required = true) promotions: Set[Promotion])

object PubResult {
  def apply(pub: Pub): PubResult = PubResult(pub.id, pub.name, pub.address, pub.addressSuburb, pub.addressState, pub.addressCountry, pub.phoneNumber, pub.website, pub.hoursOpenString, pub.longitude, pub.latitude, pub.promotions)
}


object PubProtocol {
  implicit lazy val pubFmt = Json.format[Pub]
  implicit lazy val pubResultFmt = Json.format[PubResult]
}

