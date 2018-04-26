package models

import java.time.LocalDateTime
import java.util.UUID

import io.swagger.annotations.ApiModelProperty
import org.joda.time.LocalTime
import play.api.libs.json._
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._


case class Promotion(@ApiModelProperty(required = true) id: UUID,
                     @ApiModelProperty(required = true) created: LocalDateTime,
                     @ApiModelProperty(required = true) pubId: UUID,
                     @ApiModelProperty(required = true) startTime: LocalTime,
                     @ApiModelProperty(required = true) endTime: LocalTime,
                     @ApiModelProperty(required = true) nextDayFinish: Boolean,
                     @ApiModelProperty(required = true) description: String,
                     @ApiModelProperty(required = true) monday: Boolean,
                     @ApiModelProperty(required = true) tuesday: Boolean,
                     @ApiModelProperty(required = true) wednesday: Boolean,
                     @ApiModelProperty(required = true) thursday: Boolean,
                     @ApiModelProperty(required = true) friday: Boolean,
                     @ApiModelProperty(required = true) saturday: Boolean,
                     @ApiModelProperty(required = true) sunday: Boolean,
                     @ApiModelProperty(required = true) enabled: Boolean,
                     @ApiModelProperty(required = true) serviceTypes: Set[ServiceType]) {

  def withServiceTypes(sTypes: Set[ServiceType]) = this.copy(serviceTypes = serviceTypes ++ sTypes)

}

object PromotionProtocol {
  implicit lazy val serviceTypeFmt = Json.format[ServiceType]
  implicit lazy val promotionFmt = Json.format[Promotion]
}
