package models

import java.util.UUID

import org.joda.time.DateTime

case class UserActivity(id: UUID = UUID.randomUUID(),
                        timeStamp: DateTime = new DateTime(),
                        userId: UUID,
                        pubId: Option[UUID],
                        promotionId: Option[UUID],
                        activity: models.enum.UserActivity.UserActivity) {
}
