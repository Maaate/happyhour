package models

import com.google.firebase.auth.FirebaseToken

case class AuthenticationToken(uid: String, email: String, name: String)

object AuthenticationToken {

  def apply(firebaseToken: FirebaseToken): AuthenticationToken = AuthenticationToken(firebaseToken.getUid, firebaseToken.getEmail, firebaseToken.getName)

}
