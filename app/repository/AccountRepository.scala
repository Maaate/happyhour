package repository

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.Future

import anorm.SqlParser.str
import anorm.{SQL, ~, _}
import models.Account
import play.api.Logger
import play.api.db.DBApi
import repository.helpers.SimpleQuery
import util.ExecutionContexts

class AccountRepository @Inject()(val dBApi: DBApi, val executionContexts: ExecutionContexts) extends AbstractRepository {

  private val baseQuery =
    """
      SELECT
        account.id,
        account.created,
        account.name,
        account.username,
        account.password,
        account.first_name,
        account.last_name,
        account.phone
        FROM account
    """

  def get(id: UUID): Future[Account] = Future {
    db.withConnection { implicit conn =>
      SQL(baseQuery + " WHERE id = {id}")
        .on('id -> id)
        .as(AccountRepository.RowParsers.AccountParse.single)
    }
  }

  def save(account: Account): Future[Unit] = Future {
    db.withTransaction {
      implicit conn =>
        SQL(
          """INSERT INTO account (id, name, username, password, first_name, last_name, phone)
            |VALUES ({id}, {name}, {username}, {password}, {firstName}, {lastName}, {phone})
            |ON CONFLICT (id) DO UPDATE
            |SET name = {name},
            |username = {username},
            |first_name = {firstName},
            |last_name = {lastName},
            |phone = {phone}
          """.stripMargin).on(
          'id -> account.id,
          'name -> account.name,
          'username -> account.username,
          'password -> account.password,
          'firstName -> account.firstName,
          'lastName -> account.lastName,
          'phone -> account.phone
        ).execute()
    }
  }

  def updatePassword(account: Account): Future[Unit] = Future {
    db.withTransaction {
      implicit conn =>
        SQL(
          """UPDATE account
            |SET password = {password}
            | WHERE id = {id}
          """.stripMargin)
          .on('id -> account.id,
            'password -> account.password)
          .execute()
    }
  }

  def list(searchQuery: SimpleQuery): Future[List[Account]] = Future {
    db.withConnection { implicit conn =>
      SQL(baseQuery)
        .as(AccountRepository.RowParsers.AccountParse.*)
    }
  }
}


object AccountRepository extends AnormColumnTypes {
  private val logger = Logger(this.getClass.getCanonicalName)

  object RowDefinitions {
    val AccountRow = uuidFromString("account.id") ~
      localDateTime("account.created") ~
      str("account.name") ~
      str("account.username") ~
      str("account.password") ~
      str("account.first_name") ~
      str("account.last_name") ~
      str("account.phone")
  }

  object RowParsers {
    val AccountParse = (RowDefinitions.AccountRow).map {
      case id ~ created ~ name ~ username ~ password ~ firstName ~ lastName ~ phone => Account(id, created, name, username, password, firstName, lastName, phone)
    }
  }

}
