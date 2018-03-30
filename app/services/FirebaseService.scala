package services

import java.io.ByteArrayInputStream

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.{FirebaseApp, FirebaseOptions}
import javax.inject.{Inject, Singleton}
import play.api.Configuration
import repository.UserRepository

@Singleton
class FirebaseService @Inject()(userRepository: UserRepository, config: Configuration) {


  val serviceType = config.get[String]("external.firebase.serviceType")
  val projectId = config.get[String]("external.firebase.projectId")
  val privateKeyId = config.get[String]("external.firebase.privateKeyId")
  val privateKey = config.get[String]("external.firebase.privateKey")
  val clientEmail = config.get[String]("external.firebase.clientEmail")
  val clientId = config.get[String]("external.firebase.clientId")
  val authUri = config.get[String]("external.firebase.authUri")
  val tokenUri = config.get[String]("external.firebase.tokenUri")
  val authProviderX509CertUrl = config.get[String]("external.firebase.authProviderCertUrl")
  val clientX509CertUrl = config.get[String]("external.firebase.clientCertUrl")


  val code =
    s"""{
      |  "type": "${serviceType}",
      |  "project_id": "${projectId}",
      |  "private_key_id": "${privateKeyId}",
      |  "private_key": "${privateKey}",
      |  "client_email": "${clientEmail}",
      |  "client_id": "${clientId}",
      |  "auth_uri": "${authUri}",
      |  "token_uri": "${tokenUri}",
      |  "auth_provider_x509_cert_url": "${authProviderX509CertUrl}",
      |  "client_x509_cert_url": "${clientX509CertUrl}"
      |}
      |""".stripMargin


  val stream = new ByteArrayInputStream(code.getBytes("UTF-8"))

  val options = new FirebaseOptions.Builder().setCredentials(GoogleCredentials.fromStream(stream)).setDatabaseUrl("https://happyhourandroid.firebaseio.com").build()

  val app = FirebaseApp.initializeApp(options)


  def validateToken(token: String) =
    try {
      Right(FirebaseAuth.getInstance().verifyIdTokenAsync(token).get())
    } catch {
      case e: Exception => Left(e)
    }

}
