package controllers.security

import javax.inject.Inject

import _root_.services.user.UserService
import com.mohiva.play.silhouette.api.{Silhouette, _}
import com.mohiva.play.silhouette.api.services.AuthInfoService
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models.user._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import play.api.mvc._
import rest.common.Error
import rest.models._
import utils.common.error.ErrorInfo
import utils.error.UserErrors

import scala.concurrent.Future

/**
* This controller manage registration of an user
*/
class SignUpController @Inject() (
  implicit val env: Environment[User, JWTAuthenticator],
  val userService: UserService,
  val passwordHasher: PasswordHasher,
  val authInfoService: AuthInfoService)
  extends Silhouette[User, JWTAuthenticator] {

  /**
   * Registers a new user.
   *
   * receive call with json like this:
   * 	{
   * 		"password": "",
   * 		"identifier": "",
   *  	"username": "",
   * 	}
   *
   * @return The result to display.
   */
  def signUp = Action.async(parse.json) { implicit request =>
    request.body.validate[SignUp].map { signUp =>
      val loginInfo = LoginInfo(CredentialsProvider.ID, signUp.identifier)
      userService.retrieve(loginInfo).flatMap {
        case None => /* user not already exists */
          val authInfo = passwordHasher.hash(signUp.password)
          val user = userService.signUp(loginInfo, signUp)
          user.fold(
            error => Future.successful(Conflict(Error(error))),
            user =>
              for {
                authInfo <- authInfoService.save(loginInfo, authInfo)
                authenticator <- env.authenticatorService.create(loginInfo)
                token <- env.authenticatorService.init(authenticator)
                result <- env.authenticatorService.embed(token, Future.successful {
                  Ok(Json.toJson(Token(token = token, expiresOn = authenticator.expirationDate)))
                })
              } yield {
                env.eventBus.publish(SignUpEvent(user, request, request2lang))
                env.eventBus.publish(LoginEvent(user, request, request2lang))
                //            mailService.sendWelcomeEmail(user)
                result
              }
          )
        case Some(u) => /* user already exists! */
          Future.successful(Conflict(Error(UserErrors.UserAlreadyExists)))
      }
    }.recoverTotal {
      case error =>
        Future.successful(
          BadRequest(Error(ErrorInfo.JsonFormatError, JsError.toFlatJson(error).toString()))
        )
    }
  }

  /**
   * Handles the Sign Out action.
   *
   * @return The result to display.
   */
  def signOut = SecuredAction.async { implicit request =>
    env.eventBus.publish(LogoutEvent(request.identity, request, request2lang))
    request.authenticator.discard(Future.successful(Ok))
  }
}
