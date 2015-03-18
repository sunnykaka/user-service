package utils

import com.google.inject.{Guice, Injector}
import com.mohiva.play.silhouette.api.{Logger, SecuredSettings}
import play.api.{GlobalSettings, _}
import play.api.i18n.Lang
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc.{RequestHeader, Result}
import play.api.mvc.Results._
import rest.common.Error
import utils.common.error.ErrorInfo
import utils.di.{UserModule, SilhouetteModule}
import utils.error.UserErrors

import scala.concurrent.Future

/**
 * The global configuration.
 */
object Global extends GlobalSettings with SecuredSettings with Logger {

  /**
   * The Guice dependencies injector.
   */
  var injector: Injector = _

  override def onStart(app: play.api.Application) = {
    super.onStart(app)
    // Now the configuration is read and we can create our Injector.
    injector = Guice.createInjector(
      new SilhouetteModule,
      new UserModule
    )
  }

  /**
   * Loads the controller classes with the Guice injector,
   * in order to be able to inject dependencies directly into the controller.
   *
   * @param controllerClass The controller class to instantiate.
   * @return The instance of the controller class.
   * @throws Exception if the controller couldn't be instantiated.
   */
  override def getControllerInstance[A](controllerClass: Class[A]) = injector.getInstance(controllerClass)

  /**
   * Called when a user is not authenticated.
   *
   * As defined by RFC 2616, the status code of the response should be 401 Unauthorized.
   *
   * @param request The request header.
   * @param lang The currently selected language.
   * @return The result to send to the client.
   */
  override def onNotAuthenticated(request: RequestHeader, lang: Lang): Option[Future[Result]] = {
    //controllers.StaticResponse.onNotAuthenticated(request, lang)
    implicit val r = request
    Some(Future { Unauthorized(Error(UserErrors.CredentialsNotCorrect)) })
  }

  /**
   * Called when a user is authenticated but not authorized.
   *
   * As defined by RFC 2616, the status code of the response should be 403 Forbidden.
   *
   * @param request The request header.
   * @param lang The currently selected language.
   * @return The result to send to the client.
   */
  override def onNotAuthorized(request: RequestHeader, lang: Lang): Option[Future[Result]] = {
    //controllers.StaticResponse.onNotAuthorized(request, lang)
    implicit val r = request
    Some(Future { Unauthorized(Error(UserErrors.CredentialsNotCorrect)) })
  }

  /**
   * When an exception accurs in yout application, the onError operation
   * will be called. The default is to use the internal framework error page:
   */
  override def onError(request: RequestHeader, ex: Throwable) = {
    Future.successful {
      implicit val r = request
      if (play.api.Play.current.mode == Mode.Dev)
        InternalServerError(Error(ErrorInfo.InternalServerError))
      else
        InternalServerError(Error(ErrorInfo.InternalServerError, ErrorInfo.InternalServerError.message + ex.getMessage))
    }
  }

  override def onHandlerNotFound(request: RequestHeader): Future[Result] = {
    Future.successful {
      implicit val r = request
      NotFound(Error(ErrorInfo.InternalServerError))
    }
  }
}
