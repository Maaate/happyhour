package models

import java.time.LocalDateTime
import java.util.UUID

case class UserActivity(id: UUID = UUID.randomUUID(),
                        timeStamp: LocalDateTime = LocalDateTime.now(),
                        userId: UUID,
                        pubId: Option[UUID],
                        promotionId: Option[UUID],
                        activity: models.enum.UserActivity.UserActivity) {
}
