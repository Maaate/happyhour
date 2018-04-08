package actions

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

import javax.inject.Inject
import models.User
import org.joda.time.DateTime
import play.api.Logger
import play.api.mvc.Results.Unauthorized
import play.api.mvc._
import repository.UserRepository
import services.FirebaseService

case class UserRequest[A](user: User, request: Request[A]) extends WrappedRequest(request)

class AuthorisedAction @Inject()(val parser: BodyParsers.Default, firebase: FirebaseService, userRepository: UserRepository)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[UserRequest, AnyContent] with ActionRefiner[Request, UserRequest] {

  private val logger = Logger(this.getClass.getCanonicalName)

  override protected def refine[A](request: Request[A]): Future[Either[Result, UserRequest[A]]] = {
    val header = request.headers.get("Authorization").getOrElse("").replaceFirst("Bearer ", "")

    logger.info(s"header: $header")

    firebase.validateToken(header) match {
      case Left(e) => Future.successful(Left(Unauthorized("Invalid credential")))
      case Right(token) => {
        val user = User(UUID.randomUUID(), new DateTime(), new DateTime(), token.getUid, token.getEmail, token.getName)
        for {
          saved <- userRepository.save(user)
        } yield {
          Right(new UserRequest(user, request))
        }
      }
    }
  }
}