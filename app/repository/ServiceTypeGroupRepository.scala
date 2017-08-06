package repository

import javax.inject.Inject

import anorm.SqlParser._
import anorm._
import models.ServiceTypeGroup
import play.api.Logger
import play.api.db.DBApi
import util.ExecutionContexts

import scala.concurrent.Future

class ServiceTypeGroupRepository @Inject()(val dBApi: DBApi, val executionContexts: ExecutionContexts) extends AbstractRepository {

  private val baseQuery = """select service_type_group.id, service_type_group.name from service_type_group"""

  def list: Future[List[ServiceTypeGroup]] = Future {
    db.withConnection { implicit conn =>
      SQL(baseQuery)
        .as(ServiceTypeGroupRepository.RowParsers.ServiceTypeGroupParse.*)
    }
  }
}

object ServiceTypeGroupRepository extends AnormColumnTypes {
  private val logger = Logger(this.getClass.getCanonicalName)

  object RowDefinitions {
    val ServiceTypeGroupRow = long("service_type_group.id").? ~
      str("service_type_group.name")
  }

  object RowParsers {
    val ServiceTypeGroupParse = (RowDefinitions.ServiceTypeGroupRow).map {
      case id ~ name => ServiceTypeGroup(id, name)
    }
  }

}
