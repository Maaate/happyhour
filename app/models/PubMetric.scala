package models

import org.joda.time.DateTime

case class PubMetric(id: Option[Long],
                     timeStamp: DateTime,
                     pubId: Long,
                     promotionId: Long,
                     note: String)
