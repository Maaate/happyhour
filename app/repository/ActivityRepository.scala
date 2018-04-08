package repository

import scala.concurrent.Future

import anorm.SQL
import javax.inject.Inject
import models.UserActivity
import play.api.db.DBApi
import util.ExecutionContexts

class ActivityRepository  @Inject()(val dBApi: DBApi, val executionContexts: ExecutionContexts) extends AbstractRepository {

  def save(userActivity: UserActivity): Future[Unit] = Future {
    db.withTransaction {
      implicit  conn =>
        SQL(
          """INSERT INTO punter_activity (id, punter_id_fk, pub_id_fk, promotion_id_fk, action)
            |VALUES ({id}, {punterId}, {pubId}, {promotionId}, {action})
          """.stripMargin
        ).on(
          'id -> userActivity.id,
          'punterId -> userActivity.userId,
          'pubId -> userActivity.pubId,
          'promotionId -> userActivity.promotionId,
          'action -> userActivity.activity.toString
        ).execute()
    }
  }

}