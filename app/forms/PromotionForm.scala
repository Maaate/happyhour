package forms

import java.util.UUID

import io.swagger.annotations.ApiModelProperty
import models.{ServiceType, Promotion}
import org.joda.time.LocalTime
import play.api.data.Form
import play.api.data.Forms._

// Only Used to make mapping in Swagger much easier.
// KEEP UPDATED
case class PromotionFormData(@ApiModelProperty(value = "Id of the promotion") id: UUID,
                             @ApiModelProperty(value = "Id of the pub", required = true, dataType = "java.util.UUID", example = "eddcd6cd-b8c3-43a9-bef6-9fad92d378ed") pubId: UUID,
                             @ApiModelProperty(value = "Start time of the promotion", required = true, dataType = "org.joda.time.LocalTime", example = "18:00") startTime: LocalTime,
                             @ApiModelProperty(value = "End time of the promotion", required = true, dataType = "org.joda.time.LocalTime", example = "22:30")  endTime: LocalTime,
                             @ApiModelProperty(value = "A description of the promotion", required = true, dataType = "String", example = "we sell free beer") description: String,
                             @ApiModelProperty(value = "Monday", required = true, dataType = "Boolean") monday: Boolean,
                             @ApiModelProperty(value = "Tuesday", required = true, dataType = "Boolean") tuesday: Boolean,
                             @ApiModelProperty(value = "Wednesday", required = true, dataType = "Boolean") wednesday: Boolean,
                             @ApiModelProperty(value = "Thursday", required = true, dataType = "Boolean") thursday: Boolean,
                             @ApiModelProperty(value = "Friday", required = true, dataType = "Boolean") friday: Boolean,
                             @ApiModelProperty(value = "Saturday", required = true, dataType = "Boolean") saturday: Boolean,
                             @ApiModelProperty(value = "Sunday", required = true, dataType = "Boolean") sunday: Boolean,
                             @ApiModelProperty(value = "Ids of Services", required = true) serviceTypes: List[Long])


object PromotionForm extends AdditionalMappings{

  def promotionForm: Form[Promotion] = Form(
    mapping(
      "id" -> default(uuid, UUID.randomUUID()),
      "pubId" -> uuid,
      "startTime" -> localTime,
      "endTime" -> localTime,
      "description" -> text,
      "monday" -> boolean,
      "tuesday" -> boolean,
      "wednesday" -> boolean,
      "thursday" -> boolean,
      "friday" -> boolean,
      "saturday" -> boolean,
      "sunday" -> boolean,
      "serviceTypes" -> set(longNumber)
    )({ (id, pubId, startTime, endTime, description, monday, tuesday, wednesday, thursday, friday, saturday, sunday, serviceTypes) => {
      Promotion(id, pubId, startTime, endTime, startTime.isAfter(endTime), description, monday, tuesday, wednesday, thursday, friday, saturday, sunday, true, serviceTypes.map(id => ServiceType(Some(id), "", 1)))
    }
    })
    ({
      case promotion: Promotion =>
        Some(promotion.id, promotion.pubId, promotion.startTime, promotion.endTime, promotion.description,  promotion.monday, promotion.tuesday, promotion.wednesday, promotion.thursday, promotion.friday, promotion.saturday, promotion.sunday, promotion.serviceTypes.map(_.id.get).toList.sorted.toSet)
    }).verifying("You need to select at least 1 day", { p =>
      p.monday || p.tuesday || p.wednesday || p.thursday || p.friday || p.saturday || p.sunday
    })
  )

}
