package services.user

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import models.user.{UserPasswordInfo, UserLoginInfo, User}
import net.codingwell.scalaguice.InjectorExtensions._
import org.joda.time.format.DateTimeFormat
import org.scalatestplus.play._
import play.api.db.slick.DB
import repositories.user.{UserPasswordInfoRepository, UserLoginInfoRepository, UserRepository}
import utils.Global

import scala.slick.driver.JdbcDriver.simple._
import scala.util.Random
import scala.util.Success
import scala.util.Failure

import play.api.libs.concurrent.Execution.Implicits._

class UserServiceSpec extends PlaySpec with OneAppPerSuite{

  "UserService" must {

    "retrieve user with loginInfo" in {
      DB.withSession { implicit session: Session =>

        val userRepository = Global.injector.instance[UserRepository]
        val userLoginInfoRepository = Global.injector.instance[UserLoginInfoRepository]
        val userPasswordInfoRepository = Global.injector.instance[UserPasswordInfoRepository]
        val userService = Global.injector.instance[UserService]

        val loginInfo = LoginInfo(providerID = randomString(8), providerKey = randomString(8))
        val passwordInfo = PasswordInfo(hasher = randomString(8), password = randomString(16), salt = Some(randomString(8)))

        val user = User(None, Some(randomString(8)), Some(randomString(8) + "@gmail.com"),
          Some(User.Gender.Male), Some(DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime("1988-07-21")))
        val userId = userRepository.insert(user)
        val userLoginInfo = UserLoginInfo(providerId = loginInfo.providerID, providerKey = loginInfo.providerKey, userId = userId)

        val userLoginInfoId = userLoginInfoRepository.insert(userLoginInfo)

        userService.save(loginInfo, passwordInfo)

        userService.find(loginInfo) onComplete {
          case Success(r) => {
            r.get mustBe passwordInfo
          }
          case Failure(t) => throw t
        }

        userService.retrieve(loginInfo) onComplete {
          case Success(r) => {
            r.get.copy(id = None) mustBe user
          }
          case Failure(t) => throw t
        }

        //删除所有
        userPasswordInfoRepository.deleteAll()
        userLoginInfoRepository.deleteAll()
        userRepository.deleteAll()
      }
    }

  }

  private def randomString(n: Int): String = {
    Random.alphanumeric.take(n).mkString
  }


}