package services

import models.AuthenticationToken

trait BaseFireBaseService {

  def validateToken(token: String): Either[Exception, AuthenticationToken]

}
