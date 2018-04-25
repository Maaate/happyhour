package repository

import scala.concurrent.Future

import anorm.SqlParser.str
import anorm.{SQL, ~, _}
import javax.inject.Inject
import models.User
import play.api.Logger
import play.api.db.DBApi
import util.ExecutionContexts
import anorm.JodaParameterMetaData._
import org.joda.time.DateTime

class UserRepository @Inject()(val dBApi: DBApi, val executionContexts: ExecutionContexts) extends AbstractRepository {

  private val baseQuery =
    """SELECT
      |punter.id,
      |punter.created,
      |punter.last_logged_in,
      |punter.uid,
      |punter.email,
      |punter.name
      |FROM punter
    """.stripMargin

  def save(user: User): Future[Unit] = Future {
    db.withTransaction {
      implicit  conn =>
        SQL(
          """INSERT INTO punter (id, uid, email, name)
            |VALUES ({id}, {uid}, {email}, {name})
            |ON CONFLICT (uid) DO UPDATE
            |SET name = {name},
            |last_logged_in = {lastLoggedIn},
            |email = {email}
          """.stripMargin
        ).on(
          'id -> user.id,
          'lastLoggedIn -> new DateTime(),
          'uid -> user.uid,
          'email -> user.email,
          'name -> user.name
        ).execute()
    }
  }

  def getByFirebaseUid(uid: String): Future[User] = Future{
    db.withTransaction { implicit conn =>
      SQL(baseQuery + " WHERE punter.uid = {uid}")
        .on('uid -> uid)
        .as(UserRepository.RowParser.UserParse.single)
    }
  }

}

object UserRepository extends AnormColumnTypes {

  private val logger = Logger(this.getClass.getCanonicalName)

  object RowDefinitions {
    val UserRow = uuidFromString("punter.id") ~
      localDateTime("punter.created") ~
      localDateTime("punter.last_logged_in") ~
      str("punter.uid") ~
      str("punter.email") ~
      str("punter.name")
  }

  object RowParser {
    val UserParse = (RowDefinitions.UserRow).map {
      case id ~ created ~ lastLoggedIn ~ uid ~ email ~ name => User(id, created, lastLoggedIn, uid, email, name)
    }
  }

}
