package repository

import _root_.util.ExecutionContexts
import play.api.db.DBApi

trait AbstractRepository extends AnormColumnTypes with QueryBuilder {

  def executionContexts: ExecutionContexts
  def dBApi: DBApi

  implicit lazy val executionContext = executionContexts.dbExecutionContext

  def db = dBApi.database("default")

}
