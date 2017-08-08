package repository

import javax.inject.Inject

import anorm.SqlParser._
import anorm._
import models.ServiceType
import play.api.Logger
import play.api.db.DBApi
import util.ExecutionContexts

import scala.concurrent.Future

class ServiceTypeRepository @Inject()(val dBApi: DBApi, val executionContexts: ExecutionContexts) extends AbstractRepository {

  private val baseQuery = """select service_type.id, service_type.name, service_type.service_type_group_id_fk from service_type"""

  def list: Future[List[ServiceType]] = Future {
    db.withConnection { implicit conn =>
      SQL(baseQuery)
        .as(ServiceTypeRepository.RowParsers.ServiceTypeParse.*)
    }
  }
}

object ServiceTypeRepository extends AnormColumnTypes {
  private val logger = Logger(this.getClass.getCanonicalName)

  object RowDefinitions {
    val ServiceTypeRow = long("service_type.id").? ~
      str("service_type.name") ~
      long("service_type.service_type_group_id_fk")

  }

  object RowParsers {
    val ServiceTypeParse = (RowDefinitions.ServiceTypeRow).map {
      case id ~ name ~ serviceTypeGroupId=> ServiceType(id, name, serviceTypeGroupId)
    }
  }

}
