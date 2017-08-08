package models

import io.swagger.annotations.ApiModelProperty
import play.api.libs.json.Json

case class ServiceType(@ApiModelProperty(required = true, dataType = "scala.Long") id: Option[Long], @ApiModelProperty(required = true) name:String, @ApiModelProperty(required = true) serviceTypeGroupId: Long)

object ServiceTypeProtocol {
  implicit lazy val serviceTypeFmt = Json.format[ServiceType]
}
