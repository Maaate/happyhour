package repository

import scala.concurrent.Future

import anorm.SqlParser.str
import anorm.{SQL, ~, _}
import javax.inject.Inject
import models.reports.UseActivityReport
import play.api.Logger
import play.api.db.DBApi
import util.ExecutionContexts

class ReportsRepository @Inject()(val dBApi: DBApi, val executionContexts: ExecutionContexts) extends AbstractRepository {

  def recentActivityReport: Future[List[UseActivityReport]] = Future{
    db.withTransaction {
      implicit conn =>
        SQL("""select punter.name, punter_activity.timestamp, pub.name, promotion.description from punter punter
              |inner join punter_activity punter_activity on punter.id = punter_activity.punter_id_fk
              |inner join promotion promotion on punter_activity.promotion_id_fk = promotion.id
              |inner join pub pub on punter_activity.pub_id_fk = pub.id
              |order by punter_activity.timestamp desc;
            """.stripMargin)
          .as(ReportsRepository.RowParser.ActivityReportParse.*)
    }
  }

}

object ReportsRepository extends AnormColumnTypes {

  private val logger = Logger(this.getClass.getCanonicalName)

  object RowDefinitions {
    val ActivityReportRow = str("punter.name") ~
      localDateTime("punter_activity.timestamp") ~
      str("pub.name") ~
      str("promotion.description")
  }

  object RowParser {
    val ActivityReportParse = (RowDefinitions.ActivityReportRow).map {
      case userName ~ time ~ pubName ~ description => UseActivityReport(userName, time, pubName, description)
    }
  }

}
