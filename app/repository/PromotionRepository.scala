package repository

import java.util.UUID

import javax.inject.Inject
import anorm.JodaParameterMetaData._
import anorm.SqlParser._
import anorm._
import models.{Promotion, Pub}
import play.api.Logger
import play.api.db.DBApi
import util.ExecutionContexts
import scala.concurrent.Future

class PromotionRepository @Inject()(val dBApi: DBApi, val executionContexts: ExecutionContexts) extends AbstractRepository {

  val baseQuery =
    """SELECT
      | promotion.id,
      | promotion.pub_id_fk,
      | promotion.start_time,
      | promotion.end_time,
      | promotion.description,
      | promotion.monday,
      | promotion.tuesday,
      | promotion.wednesday,
      | promotion.thursday,
      | promotion.friday,
      | promotion.saturday,
      | promotion.sunday,
      | promotion.next_day_finish,
      | promotion.enabled,
      | service_type.id,
      | service_type.name,
      | service_type.service_type_group_id_fk
      | FROM promotion
      | LEFT JOIN promotion_service_type ON promotion_service_type.promotion_id_fk = promotion.id
      | LEFT JOIN service_type ON service_type.id = promotion_service_type.service_type_id_fk
    """.stripMargin

  def get(id: UUID): Future[Promotion] = Future {
    db.withConnection { implicit conn =>
      SQL(baseQuery + "WHERE promotion.id = {id}")
        .on('id -> id)
        .as(PromotionRepository.RowParsers.PromotionParse.*) reduce (combine)
    }
  }

  def getSimilar(promotion: Promotion) = {
    Future {
      db.withConnection {
        implicit conn =>
          SQL(baseQuery +
            """WHERE promotion.pub_id_fk = {pubId}
              | AND start_time = {startTime}
              | AND end_time = {endTime}
              | AND monday = {monday}
              | AND tuesday = {tuesday}
              | AND wednesday = {wednesday}
              | AND thursday = {thursday}
              | AND friday = {friday}
              | AND saturday = {saturday}
              | AND sunday = {sunday}
            """.stripMargin)
            .on('pubId -> promotion.pubId,
              'startTime -> promotion.startTime.toDateTimeToday,
              'endTime -> promotion.endTime.toDateTimeToday,
              'monday -> promotion.monday,
              'tuesday -> promotion.tuesday,
              'wednesday -> promotion.wednesday,
              'thursday -> promotion.thursday,
              'friday -> promotion.friday,
              'saturday -> promotion.saturday,
              'sunday -> promotion.sunday)
            .as(PromotionRepository.RowParsers.PromotionParse.*).foldLeft(List[Promotion]()) {
            case (existing: List[Promotion], p: Promotion) => {
              existing.filterNot(_.id == p.id) :+ combine(existing.find(_.id == p.id).getOrElse(p), p)
            }
          }
      }
    }
  }

  def save(promotion: Promotion): Future[Unit] = Future {
    db.withTransaction {
      implicit conn =>
        SQL(
          """INSERT INTO promotion(id,  pub_id_fk, start_time, end_time, description, monday, tuesday, wednesday, thursday, friday, saturday, sunday, next_day_finish, enabled)
            | VALUES ({id}, {pubId}, {startTime}, {endTime}, {description}, {monday}, {tuesday}, {wednesday}, {thursday}, {friday}, {saturday}, {sunday}, {nextDayFinish}, {enabled})
            | ON CONFLICT (id) DO UPDATE
            | SET start_time = {startTime},
            | end_time = {endTime},
            | description = {description},
            | monday = {monday},
            | tuesday = {tuesday},
            | wednesday = {wednesday},
            | thursday = {thursday},
            | friday = {friday},
            | saturday = {saturday},
            | sunday = {sunday},
            | next_day_finish = {nextDayFinish}
          """.stripMargin)
          .on('id -> promotion.id,
            'pubId -> promotion.pubId,
            'startTime -> promotion.startTime.toDateTimeToday,
            'endTime -> promotion.endTime.toDateTimeToday,
            'description -> promotion.description,
            'monday -> promotion.monday,
            'tuesday -> promotion.tuesday,
            'wednesday -> promotion.wednesday,
            'thursday -> promotion.thursday,
            'friday -> promotion.friday,
            'saturday -> promotion.saturday,
            'sunday -> promotion.sunday,
            'nextDayFinish -> promotion.nextDayFinish,
            'enabled -> promotion.enabled
          ).execute()

        SQL("""DELETE FROM promotion_service_type where promotion_id_fk = {promotionId}""").on('promotionId -> promotion.id).execute()


        val updateQuery =
          """INSERT INTO promotion_service_type(promotion_id_fk, service_type_id_fk)
            | VALUES ({promotionId}, {serviceTypeId})
          """.stripMargin
        val params = promotion.serviceTypes.map{
          serviceType => Seq(NamedParameter("promotionId", promotion.id), NamedParameter("serviceTypeId", serviceType.id.get))
        }.toList
        params match {
          case Nil => ()
          case head :: Nil => SQL(updateQuery).on(head: _*).execute()
          case head :: tail => BatchSql(updateQuery, head, tail: _*).execute()
        }
    }
  }

  def setEnabled(id: UUID, enabled: Boolean): Future[Unit] = Future {
    db.withTransaction {
      implicit conn =>
        SQL(
          s"""UPDATE promotion
             |SET enabled = ${enabled}
             |WHERE id = '${id}'
          """.stripMargin).execute()
    }

  }

  private def combine(p1: Promotion, p2: Promotion): Promotion = {
    if (p1.id != p2.id) {
      throw new IllegalArgumentException("Cannot combine different promotion")
    }
    p1.withServiceTypes(p2.serviceTypes)
  }
}

object PromotionRepository extends AnormColumnTypes {
  private val logger = Logger(this.getClass.getCanonicalName)

  object RowDefinitions {
    val promotionRow = uuidFromString("promotion.id") ~
      uuidFromString("promotion.pub_id_fk") ~
      localTime("promotion.start_time") ~
      localTime("promotion.end_time") ~
      bool("promotion.next_day_finish") ~
      str("promotion.description") ~
      bool("promotion.monday") ~
      bool("promotion.tuesday") ~
      bool("promotion.wednesday") ~
      bool("promotion.thursday") ~
      bool("promotion.friday") ~
      bool("promotion.saturday") ~
      bool("promotion.sunday") ~
      bool("promotion.enabled")
  }

  object RowParsers {
    val PromotionParse = (RowDefinitions.promotionRow ~ ServiceTypeRepository.RowParsers.ServiceTypeParse.?).map {
      case id ~ pubId ~ startTime ~ endTime ~ nextDayFinish ~ description ~ monday ~ tuesday ~ wednesday ~ thursday ~ friday ~ saturday ~ sunday ~ enabled ~ services =>
        Promotion(id, pubId, startTime, endTime, nextDayFinish, description, monday, tuesday, wednesday, thursday, friday, saturday, sunday, enabled, services.toSet)
    }
  }

}