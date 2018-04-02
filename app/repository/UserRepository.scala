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

}

object UserRepository extends AnormColumnTypes {

  private val logger = Logger(this.getClass.getCanonicalName)

  object RowDefinitions {
    val UserRow = uuidFromString("punter.id") ~
      dateTime("punter.created") ~
      dateTime("punter.last_logged_in") ~
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
