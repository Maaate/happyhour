package forms

import org.joda.time.LocalTime
import play.api.data.Forms._
import play.api.data._
import play.api.data.Mapping

private[forms] trait AdditionalMappings {

  def localTime: Mapping[LocalTime] = nonEmptyText.transform({
    s => LocalTime.parse(s)
  }, _.toString())

}
