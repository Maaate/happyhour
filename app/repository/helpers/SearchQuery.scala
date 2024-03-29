package repository.helpers

import org.joda.time.LocalTime

sealed trait SearchQuery {
  def page: Option[Int]
  def itemsPerPage: Option[Int]
  def term: Option[String]
  def ordering: Option[Ordering]
}

case class SimpleQuery(page: Option[Int] = None,
                       itemsPerPage: Option[Int] = None,
                       term: Option[String] = None,
                       ordering: Option[Ordering] = None) extends SearchQuery

case class FullPubSearchQuery (location: Location = Location(),
                               currentTime: Option[LocalTime] = None,
                               dayOfWeek: Option[String] = None,
                               serviceTypeGroupIds: List[Long] = List(),
                               serviceTypeIds: List[Long] = List(),
                               current: Boolean = false,
                               googleId: Option[String] = None,
                               pubName: Option[String] = None,
                               page: Option[Int] = None,
                               itemsPerPage: Option[Int] = None,
                               term: Option[String] = None,
                               ordering: Option[Ordering] = None)extends SearchQuery
