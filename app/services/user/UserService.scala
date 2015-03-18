package services.user

import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.impl.daos.DelegableAuthInfoDAO
import models.user.{User, UserLoginInfo, UserPasswordInfo}
import play.api.Play.current
import play.api.db.slick.DB
import repositories.user.{UserLoginInfoRepository, UserPasswordInfoRepository, UserRepository}
import rest.models.SignUp
import utils.common.error.ErrorInfo
import utils.error.UserErrors

import scala.concurrent.Future

/**
 * Handles actions to users.
 */
class UserService @Inject() (
  userRepository: UserRepository,
  userLoginInfoRepository: UserLoginInfoRepository,
  userPasswordInfoRepository: UserPasswordInfoRepository
) extends DelegableAuthInfoDAO[PasswordInfo] with IdentityService[User] {

  def signUp(info: LoginInfo, up: SignUp): Either[ErrorInfo, User] = {
    DB.withTransaction { implicit session =>
      if(userRepository.findByUsername(up.username).isEmpty) {
        if(userRepository.findByEmail(up.identifier).isEmpty) {

          val user = User(username = Some(up.username), email = Some(up.identifier))
          val userAfterSave = user.copy(id = Some(userRepository.save(user)))
          val uli = UserLoginInfo(providerId = info.providerID, providerKey = info.providerKey, userId = userAfterSave.id.get)
          userLoginInfoRepository.save(uli)
          Right(userAfterSave)

        } else Left(UserErrors.UserAlreadyExists.copy(message = s"邮箱${up.identifier}重复"))
      } else Left(UserErrors.UserAlreadyExists.copy(message = s"用户名${up.username}重复"))
    }
  }

  /**
   * Retrieves a user that matches the specified login info.
   *
   * @param loginInfo The login info to retrieve a user.
   * @return The retrieved user or None if no user could be retrieved for the given login info.
   */
  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = {

    Future.successful {
      DB.withTransaction { implicit session =>
        val userLoginInfoOption = userLoginInfoRepository.findByProviderIdKey(loginInfo.providerID, loginInfo.providerKey)
        userLoginInfoOption.flatMap { userLoginInfo =>
          userRepository.findById(userLoginInfo.userId)
        }
      }
    }
  }

  override def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    Future.successful {
      DB.withTransaction { implicit session =>
        val userLoginInfoOption = userLoginInfoRepository.findByProviderIdKey(loginInfo.providerID, loginInfo.providerKey)
        println(userLoginInfoOption)
        val userPasswordInfo = new UserPasswordInfo(id = None, hasher = authInfo.hasher, password = authInfo.password,
          salt = authInfo.salt, userLoginInfoId = userLoginInfoOption.get.id.get)

        userPasswordInfoRepository.save(userPasswordInfo)
        authInfo
      }
    }
  }

  override def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = {

    Future.successful {
      DB.withTransaction { implicit session =>
        val userLoginInfoOption = userLoginInfoRepository.findByProviderIdKey(loginInfo.providerID, loginInfo.providerKey)
        //      userLoginInfoOption.flatMap(x => {
        //        val userPasswordInfoOption = userPasswordInfoRepository.findByUserLoginId(x.id.get)
        //        userPasswordInfoOption.map(userPasswordInfo => new PasswordInfo(hasher = userPasswordInfo.hasher, password = userPasswordInfo.password,
        //          salt = Some(userPasswordInfo.salt)))
        //      })

        for {
          userLoginInfo <- userLoginInfoOption
          userPasswordInfo <- userPasswordInfoRepository.findByUserLoginId(userLoginInfo.id.get)
        } yield {
          new PasswordInfo(hasher = userPasswordInfo.hasher, password = userPasswordInfo.password,
            salt = userPasswordInfo.salt)
        }
      }
    }
  }
}