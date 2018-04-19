package services

import scala.concurrent.Future

import javax.inject.Inject
import models.{Promotion, User, UserActivity}
import repository.{ActivityRepository, PromotionRepository}

class PromotionService @Inject()(promotionRepository: PromotionRepository, activityRepository: ActivityRepository) {

  def save(promotion: Promotion, userOption: Option[User])  = {
    promotionRepository.save(promotion)
    userOption match {
      //case Some(user) => activityRepository.save(UserActivity(userId = user.id, pubId = Some(promotion.pubId), promotionId = Some(promotion.id), activity = models.enum.UserActivity.CreatePromotion))
      case _ => Future.successful(())
    }

  }
}
