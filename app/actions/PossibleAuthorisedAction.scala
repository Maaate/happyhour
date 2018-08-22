package actions

import java.time.LocalDateTime
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

import javax.inject.Inject
import models.User
import play.api.Logger
import play.api.mvc._
import repository.UserRepository
import services.BaseFireBaseService

class PossibleAuthorisedAction @Inject()(val parser: BodyParsers.Default, override val baseFireBaseService: BaseFireBaseService, override val userRepository: UserRepository)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[UserRequest, AnyContent] with ActionTransformer[Request, UserRequest] with BaseAuthorisationAction{

  override protected def transform[A](request: Request[A]): Future[UserRequest[A]] = {
    val header = request.headers.get("Authorization").getOrElse("").replaceFirst("Bearer ", "")
    logger.debug(s"header: $header")

    baseFireBaseService.validateToken(header) match {
      case Left(e) => Future.successful(UserRequest(None, request))
      case Right(token) => Future.successful(UserRequest(Some(User(UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now(), token.uid, token.email, token.name)), request))
    }
  }

}
