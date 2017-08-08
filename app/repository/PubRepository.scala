package repository

import java.util.UUID
import javax.inject.Inject

import anorm.JodaParameterMetaData._
import anorm.SqlParser._
import anorm._
import models.Pub
import play.api.Logger
import play.api.db.DBApi
import repository.helpers.{Ascending, FullPubSearchQuery, Location, SimpleQuery}
import util.ExecutionContexts

import scala.concurrent.Future

class PubRepository @Inject()(val dBApi: DBApi, val executionContexts: ExecutionContexts) extends AbstractRepository {

  def baseQuery(location: Option[Location] = None) =
    s"""SELECT
      |  pub.id,
      |  pub.google_id,
      |  pub.account_id_fk,
      |  pub.name,
      |  pub.address,
      |  pub.address_suburb,
      |  pub.address_state,
      |  pub.address_country,
      |  pub.longitude,
      |  pub.latitude,
      |  pub.website_url,
      |  pub.phone_number,
      |  pub.hours,
      |  pub.last_updated,
      |  pub.enabled,
      |  ${distanceQuery(location)}
      |  promotion.id,
      |  promotion.pub_id_fk,
      |  promotion.start_time,
      |  promotion.end_time,
      |  promotion.description,
      |  promotion.monday,
      |  promotion.tuesday,
      |  promotion.wednesday,
      |  promotion.thursday,
      |  promotion.friday,
      |  promotion.saturday,
      |  promotion.sunday,
      |  promotion.next_day_finish,
      |  promotion.enabled,
      |  service_type.id,
      |  service_type.name,
      |  service_type.service_type_group_id_fk
      |FROM pub
      |  LEFT OUTER JOIN promotion ON promotion.pub_id_fk = pub.id
      |  LEFT JOIN promotion_service_type ON promotion_service_type.promotion_id_fk = promotion.id
      |  LEFT JOIN service_type ON service_type.id = promotion_service_type.service_type_id_fk
    """.stripMargin

  def get(id: UUID): Future[Pub] = Future {
    db.withConnection { implicit conn =>
      SQL(baseQuery() + "WHERE pub.id = {id}")
        .on('id -> id)
        .as(PubRepository.RowParsers.PubParse.*) reduce (combine)
    }
  }

  def save(pub: Pub): Future[Unit] = Future {
    db.withTransaction {
      implicit conn =>
        SQL(
          """INSERT INTO pub (id, google_id, account_id_fk, name, address, address_suburb, address_state, address_country, longitude, latitude, website_url, phone_number, hours, last_updated, enabled)
            |VALUES ({id}, {googleId}, {accountId}, {name}, {address}, {addressSuburb}, {addressState}, {addressCountry}, {longitude}, {latitude}, {websiteUrl}, {phoneNumber}, {hours}, {lastUpdated}, {enabled})
            |ON CONFLICT (id) DO UPDATE
            |SET account_id_fk = {accountId},
            |name = {name},
            |google_id = {googleId},
            |address = {address},
            |address_suburb = {addressSuburb},
            |address_state = {addressState},
            |address_country = {addressCountry},
            |longitude = {longitude},
            |latitude = {latitude},
            |website_url = {websiteUrl},
            |phone_number = {phoneNumber},
            |hours = {hours},
            |last_updated = {lastUpdated}
          """.stripMargin)
          .on('id -> pub.id,
            'googleId -> pub.googleId,
            'accountId -> pub.accountId,
            'name -> pub.name,
            'address -> pub.address,
            'addressSuburb -> pub.addressSuburb,
            'addressState -> pub.addressState,
            'addressCountry -> pub.addressCountry,
            'longitude -> pub.longitude,
            'latitude -> pub.latitude,
            'websiteUrl -> pub.website,
            'hours -> pub.hoursOpenString,
            'phoneNumber -> pub.phoneNumber,
            'lastUpdated -> pub.lastUpdatedByGoogle,
            'enabled -> pub.enabled
          )
          .execute()
    }
  }

  def list(simpleQuery: SimpleQuery): Future[List[Pub]] = Future {
    db.withConnection { implicit conn =>
      SQL(baseQuery()).as(PubRepository.RowParsers.PubParse.*).foldLeft(List[Pub]()) {
        case (existing: List[Pub], p: Pub) => {
          existing.filterNot(_.id == p.id) :+ combine(existing.find(_.id == p.id).getOrElse(p), p)
        }
      }
    }
  }

  def search(fullPubSearchQuery: FullPubSearchQuery): Future[List[Pub]] = Future {
    db.withConnection { implicit conn =>
      val enabledParams = Seq(
        "pub.enabled = true",
        "promotion.enabled = true"
      )

      val locationParams = if (fullPubSearchQuery.location.radius == BigDecimal.valueOf(0.0)) {
        Seq()
      } else {
        Seq(
          s"earth_box( ll_to_earth(${fullPubSearchQuery.location.latitude}, ${fullPubSearchQuery.location.longitude}), ${fullPubSearchQuery.location.radius}) @> ll_to_earth(pub.latitude, pub.longitude)"
        )
      }

      val timeParams = (fullPubSearchQuery.dayOfWeek, fullPubSearchQuery.currentTime) match {
        case (Some(dayOfWeek), Some(currentTime)) => Seq(
          s"promotion.${dayOfWeek} = true",
          s"(promotion.end_time >= '${currentTime}' OR promotion.next_day_finish = true)"
        )
        case _ => Seq()
      }

      val currentParams =  fullPubSearchQuery.currentTime match {
        case Some(currentTime) if fullPubSearchQuery.current => Seq(s"promotion.start_time <= '${currentTime}'")
        case _ => Seq()
      }


      val googleParams = (fullPubSearchQuery.googleId) match {
        case (Some(googleId)) => Seq(
          s"pub.google_id = '${googleId}'"
        )
        case _ => Seq()
      }

      val params = enabledParams ++ locationParams ++ timeParams ++ currentParams ++ googleParams

      executeQuery(baseQuery(Some(fullPubSearchQuery.location)), PubRepository.RowParsers.PubParse.*, params, Some(Ascending(PubRepository.distance_column_name))).foldLeft(List[Pub]()) {
        case (existing: List[Pub], p: Pub) => {
          existing.filterNot(_.id == p.id) :+ combine(existing.find(_.id == p.id).getOrElse(p), p)
        }
      }
    }
  }

  private def combine(p1: Pub, p2: Pub): Pub = {
    if (p1.id != p2.id) {
      throw new IllegalArgumentException("Cannot combine different pubs")
    }
    p1.withPromotions(p2.promotions)
  }

  private def distanceQuery(location: Option[Location]) = location match {
    case Some(loc) => s"earth_distance(ll_to_earth( ${loc.latitude}, ${loc.longitude} ), ll_to_earth(pub.latitude, pub.longitude)) as ${PubRepository.distance_column_name},"
    case _ => ""
  }
}

object PubRepository extends AnormColumnTypes {

  val distance_column_name = "distance"

  private val logger = Logger(this.getClass.getCanonicalName)

  object RowDefinitions {
    val PubRow = uuidFromString("pub.id") ~
      str("pub.google_id").? ~
      str("pub.name") ~
      str("pub.address") ~
      str("pub.address_suburb") ~
      str("pub.address_state") ~
      str("pub.address_country") ~
      bigDecimal("pub.longitude") ~
      bigDecimal("pub.latitude") ~
      uuidFromString("pub.account_id_fk") ~
      str("pub.website_url").? ~
      str("pub.phone_number").? ~
      str("pub.hours").? ~
      dateTime("pub.last_updated").? ~
      bool("pub.enabled")
  }

  object RowParsers {
    val PubParse = (RowDefinitions.PubRow ~ PromotionRepository.RowParsers.PromotionParse.?).map {
      case id ~ googleId ~ name ~ address ~ addressSuburb ~ addressState ~ addressCountry ~ longitude ~ latitude ~ accountId ~ website ~ phoneNumber ~ hours ~ updatedByGoogle ~ enabled ~ promotions=>
        Pub(id, googleId, name, address, addressSuburb, addressState, addressCountry, longitude, latitude, accountId, website, phoneNumber, hours, updatedByGoogle, enabled, promotions.toSet)
    }
  }

}

