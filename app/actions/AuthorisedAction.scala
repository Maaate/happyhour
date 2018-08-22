package actions

import java.time.LocalDateTime
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

import javax.inject.Inject
import models.User
import play.api.Logger
import play.api.mvc.Results.Unauthorized
import play.api.mvc._
import repository.UserRepository
import services.BaseFireBaseService

case class UserRequest[A](user: Option[User], request: Request[A]) extends WrappedRequest(request)

class AuthorisedAction @Inject()(val parser: BodyParsers.Default, baseFireBaseService: BaseFireBaseService, userRepository: UserRepository)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[UserRequest, AnyContent] with ActionRefiner[Request, UserRequest] {

  private val logger = Logger(this.getClass.getCanonicalName)

  override protected def refine[A](request: Request[A]): Future[Either[Result, UserRequest[A]]] = {
    val header = request.headers.get("Authorization").getOrElse("").replaceFirst("Bearer ", "")

    logger.debug(s"header: $header")

    baseFireBaseService.validateToken(header) match {
      case Left(e) => Future.successful(Left(Unauthorized("Invalid credential")))
      case Right(token) => {
        val user = User(UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now(), token.uid, token.email, token.name)
        for {
          _ <- userRepository.save(user)
          saved <- userRepository.getByFirebaseUid(user.uid)
        } yield {
          Right(new UserRequest(Some(saved), request))
        }
      }
    }
  }
}