package repositories.user

import org.joda.time.format.DateTimeFormat
import play.api.db.slick.DB
import utils.Global
import scala.slick.driver.JdbcDriver.simple._
import models.user.User

import org.scalatest._
import org.scalatestplus.play._

import net.codingwell.scalaguice.InjectorExtensions._

class UserRepositorySpec extends PlaySpec with OneAppPerSuite{

  "Users Repository" must {

    "save and query users" in {
      DB.withSession { implicit session: Session =>

        val userRepository = Global.injector.instance[UserRepository]

        val user = User(None, Some("sunnykaka"), Some("sunnykaka0721@gmail.com"),
          Some(User.Gender.Male), Some(DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime("1988-07-21")))

        //新增
        val id1 = userRepository.save(user)
        println("insert id: " + id1)
        id1 must be > 1L

        //根据ID查找
        val userRetrieve = userRepository.findById(id1);
        userRetrieve mustBe Some(user.copy(id = Some(id1)))

        //更新
        val userUpdate = user.copy(id = Some(id1), username = Some("sunnykakaafterupdate"))
        val id2 = userRepository.save(userUpdate)
        id2 mustBe id1

        //更新后根据ID查找
        val userUpdateRetrieve = userRepository.findById(id2);
        userUpdateRetrieve.get.username mustBe Some("sunnykakaafterupdate")

        val id3 = userRepository.save(user)

        //根据ID列表查找
        val findByIdUsers = userRepository.findByIds(Seq(id1, id3))
        findByIdUsers.size mustBe 2
        findByIdUsers.foreach(_.copy(username = Some("sunnykaka"), id = None) mustBe user)

        //删除
        userRepository.deleteById(id3) mustBe 1
        userRepository.findById(id3) mustBe None

        //查询所有
        val users = userRepository.findAll()
        users.size must be >= 1

      }
    }

  }

}