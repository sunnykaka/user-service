package controllers.security

import javax.inject.Inject

import _root_.services.user.UserService
import com.mohiva.play.silhouette.api.{Silhouette, _}
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.util.Credentials
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.impl.exceptions.{InvalidPasswordException, IdentityNotFoundException}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models.user.User
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import play.api.mvc._
import rest.common.Error
import rest.models._
import utils.common.error.ErrorInfo
import utils.error.UserErrors

import scala.concurrent.Future

/**
* This controller manage authentication of an user by identifier and password
*/
class SignInController @Inject() (
  implicit val env: Environment[User, JWTAuthenticator],
  val userService: UserService)
  extends Silhouette[User, JWTAuthenticator] {

  /**
   * Authenticates a user against the credentials provider.
   *
   * receive json like this:
   * {
   * 	"identifier": "...",
   *  "password": "..."
   * }
   *
   * @return The result to display.
   */
  def signIn = Action.async(parse.json) { implicit request =>
    request.body.validate[Credentials].map { credentials =>
      (env.providers.get(CredentialsProvider.ID) match {
        case Some(p: CredentialsProvider) => p.authenticate(credentials)
        case _                            => Future.failed(new ProviderException("Cannot find credentials provider"))
      }).flatMap { loginInfo =>
        userService.retrieve(loginInfo).flatMap {
          case Some(user) => env.authenticatorService.create(loginInfo).flatMap { authenticator =>
            env.eventBus.publish(LoginEvent(user, request, request2lang))
            env.authenticatorService.init(authenticator).flatMap { token =>
              env.authenticatorService.embed(token, Future.successful {
                Ok(Json.toJson(Token(token = token, expiresOn = authenticator.expirationDate)))
              })
            }
          }
          case None =>
            Future.failed(new IdentityNotFoundException("Couldn't find user"))
        }
      }.recoverWith(exceptionHandler orElse loginExceptionHandler)
    }.recoverTotal {
      case error => Future.successful(BadRequest(Error(ErrorInfo.JsonFormatError, JsError.toFlatJson(error).toString())))
    }
  }

  protected def loginExceptionHandler(implicit request: RequestHeader): PartialFunction[Throwable, Future[Result]] = {
    case e: IdentityNotFoundException =>
      Future { Unauthorized(Error(UserErrors.CredentialsNotCorrect)) }
    case e: InvalidPasswordException =>
      Future { Unauthorized(Error(UserErrors.CredentialsNotCorrect)) }
    case e: ProviderException =>
      logger.warn(e.getMessage, e)
      Future { Unauthorized(Error(UserErrors.LoginFailed)) }
  }

}

