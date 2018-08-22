package actions

import java.time.LocalDateTime
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

import models.User
import play.api.Logger
import play.api.mvc.Results.Unauthorized
import play.api.mvc.{Request, WrappedRequest}
import repository.UserRepository
import services.BaseFireBaseService

trait BaseAuthorisationAction {

  def baseFireBaseService: BaseFireBaseService
  def userRepository: UserRepository

  implicit def executionContext: ExecutionContext

  val logger = Logger(this.getClass.getCanonicalName)

  def saveUser[A](request: Request[A]) = {
    val header = request.headers.get("Authorization").getOrElse("").replaceFirst("Bearer ", "")

    logger.debug(s"header: $header")

    baseFireBaseService.validateToken(header) match {
      case Left(e) => Future.successful(Left(Unauthorized("Invalid credential")))
      case Right(token) => {
        val user = User(UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now(), token.uid, token.email, token.name)
        for {
          _ <- userRepository.save(user)
          saved <- userRepository.getByFirebaseUid(user.googleUid)
        } yield {
          Right(new UserRequest(Some(saved), request))
        }
      }
    }
  }

}

case class UserRequest[A](user: Option[User], request: Request[A]) extends WrappedRequest(request)
