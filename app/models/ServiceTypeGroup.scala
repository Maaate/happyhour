package models

import io.swagger.annotations.ApiModelProperty
import play.api.libs.json.Json

case class ServiceTypeGroup(@ApiModelProperty(required = true, dataType = "scala.Long") id: Option[Long], @ApiModelProperty(required = true) name:String)

object ServiceTypeGroupProtocol {
  implicit lazy val serviceTypeGroupFmt = Json.format[ServiceTypeGroup]
}