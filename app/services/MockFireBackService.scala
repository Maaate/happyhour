package services

import javax.inject.Singleton
import models.AuthenticationToken


@Singleton
class MockFireBackService extends BaseFireBaseService {

  def validateToken(token: String): Either[Exception, AuthenticationToken] = if(token == "invalid") {
    Left(throw new Exception("is just fake stuff yeah"))
  } else {
    Right(AuthenticationToken("1234567890", "banana@apple.com", "John Smith"))
  }

}
