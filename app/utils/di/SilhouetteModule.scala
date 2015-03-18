package utils.di

import com.google.inject.{AbstractModule, Provides}
import com.mohiva.play.silhouette.api.{LoginInfo, EventBus, Environment}
import com.mohiva.play.silhouette.api.services.{IdentityService, AuthInfoService, AuthenticatorService}
import com.mohiva.play.silhouette.api.util._
import com.mohiva.play.silhouette.impl.authenticators.{JWTAuthenticatorService, JWTAuthenticatorSettings, JWTAuthenticator}
import com.mohiva.play.silhouette.impl.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.impl.services.DelegableAuthInfoService
import com.mohiva.play.silhouette.impl.util.{BCryptPasswordHasher, SecureRandomIDGenerator, PlayCacheLayer}
import net.codingwell.scalaguice.ScalaModule
import play.Logger
import play.api.Play
import play.api.Play.current
import repositories.user.UserRepository
import repositories.user.impl.UserRepositoryImpl
import models.user.User
import services.user.UserService

import scala.concurrent.Future

/**
 * The Guice module which wires all Silhouette dependencies.
 */
class SilhouetteModule extends AbstractModule with ScalaModule {

  /**
   * Configures the module.
   */
  def configure() {
    Logger.debug("Binding to Slick DAO implementations.")
    bind[UserRepository].to[UserRepositoryImpl]
    bind[DelegableAuthInfoDAO[PasswordInfo]].to[UserService]

    bind[CacheLayer].to[PlayCacheLayer]
    bind[HTTPLayer].to[PlayHTTPLayer]
    bind[IDGenerator].toInstance(new SecureRandomIDGenerator())
    bind[PasswordHasher].toInstance(new BCryptPasswordHasher)
    bind[EventBus].toInstance(EventBus())
  }

  /**
   * Provides the Silhouette environment.
   *
   * @param userService The user service implementation.
   * @param authenticatorService The authentication service implementation.
   * @param eventBus The event bus instance.
   * @return The Silhouette environment.
   */
  @Provides
  def provideEnvironment(
    userService: UserService,
    authenticatorService: AuthenticatorService[JWTAuthenticator],
    eventBus: EventBus,
    credentialsProvider: CredentialsProvider): Environment[User, JWTAuthenticator] = {

    Environment[User, JWTAuthenticator](
//            new IdentityService[User] {
//              override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = {Future.successful(None)}
//            },
      userService,
      authenticatorService,
      Map(
        credentialsProvider.id -> credentialsProvider
      ),
      eventBus
    )
  }

  /**
   * Provides the authenticator service.
   *
   * @param cacheLayer The cache layer implementation.
   * @param idGenerator The ID generator used to create the authenticator ID.
   * @return The authenticator service.
   */
  @Provides
  def provideAuthenticatorService(
    cacheLayer: CacheLayer,
    idGenerator: IDGenerator): AuthenticatorService[JWTAuthenticator] = {

    val settings = JWTAuthenticatorSettings(
      headerName = Play.configuration.getString("silhouette.authenticator.headerName").getOrElse { "X-Auth-Token" },
      issuerClaim = Play.configuration.getString("silhouette.authenticator.issueClaim").getOrElse { "play-silhouette" },
      encryptSubject = Play.configuration.getBoolean("silhouette.authenticator.encryptSubject").getOrElse { true },
      authenticatorIdleTimeout = Play.configuration.getInt("silhouette.authenticator.authenticatorIdleTimeout"), // This feature is disabled by default to prevent the generation of a new JWT on every request
      authenticatorExpiry = Play.configuration.getInt("silhouette.authenticator.authenticatorExpiry").getOrElse { 12 * 60 * 60 },
      sharedSecret = Play.configuration.getString("application.secret").get)
    new JWTAuthenticatorService(
      settings = settings,
      dao = None,
      idGenerator = idGenerator,
      clock = Clock())
  }

  /**
   * Provides the auth info service.
   *
   * @param passwordInfoDAO The implementation of the delegable password auth info DAO.
   * @return The auth info service instance.
   */
  @Provides
  def provideAuthInfoService(
    passwordInfoDAO: DelegableAuthInfoDAO[PasswordInfo]): AuthInfoService = {

    new DelegableAuthInfoService(passwordInfoDAO)
  }

  /**
   * Provides the credentials provider.
   *
   * @param authInfoService The auth info service implemenetation.
   * @param passwordHasher The default password hasher implementation.
   * @return The credentials provider.
   */
  @Provides
  def provideCredentialsProvider(
    authInfoService: AuthInfoService,
    passwordHasher: PasswordHasher): CredentialsProvider = {

    new CredentialsProvider(authInfoService, passwordHasher, Seq(passwordHasher))
  }

}
