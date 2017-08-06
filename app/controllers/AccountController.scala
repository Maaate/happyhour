package controllers

import java.util.UUID
import javax.inject.Inject
import forms.AccountForm
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import repository.AccountRepository
import repository.helpers.SimpleQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AccountController @Inject()(implicit val messagesApi: MessagesApi, accountRepository: AccountRepository) extends Controller with I18nSupport {

  def createAccount =
    Action { implicit request =>
      Ok(views.html.account.editAccount(AccountForm.accountForm))
    }

  def editAccount(id: UUID) = Action.async { implicit request =>
    for {
      account <- accountRepository.get(id)
    } yield render {
      case Accepts.Html() => Ok(views.html.account.editAccount(AccountForm.accountForm.fill(account)))
      case _ => UnsupportedMediaType("")

    }
  }


  def viewAccount(id: UUID) =
    Action.async { implicit request =>
      for {
        account <- accountRepository.get(id)
      } yield render {
        case Accepts.Html() => Ok(views.html.account.viewAccount(account))
        case _ => UnsupportedMediaType("")

      }
    }

  def listAccounts(term: Option[String] = None, page: Option[Int] = None, items: Option[Int] = None) =
    Action.async { implicit request =>
      accountRepository.list(SimpleQuery()).map {
        accounts =>
          render {
            case Accepts.Html() => Ok(views.html.account.accounts(accounts))
            case _ => UnsupportedMediaType("")
          }
      }
    }

  def saveAccount() =
    Action.async { implicit request =>
      AccountForm.accountForm.bindFromRequest().fold({
        formWithErrors => Future.successful(render {
          case Accepts.Html() => BadRequest(views.html.account.editAccount(formWithErrors))
          case _ => UnsupportedMediaType("")
        })
      }, {
        account => for {
          - <- accountRepository.save(account)
        } yield render {
          case Accepts.Html() => Ok(views.html.account.viewAccount(account))
          case _ => UnsupportedMediaType("")
        }
      })
    }
}
