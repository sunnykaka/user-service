package repositories.user.impl

import models.user.{UserPasswordInfo, UserLoginInfo}
import repositories.user.{UserPasswordInfoRepository, UserLoginInfoRepository}

import scala.slick.driver.MySQLDriver.simple._

import scala.slick.jdbc.{GetResult => GR, SetParameter => SP, StaticQuery}
import repositories.common.SlickImplicits._

import scala.slick.jdbc.StaticQuery.interpolation


/**
 * Created by liubin on 15-3-12.
 */
class UserPasswordInfoRepositoryImpl extends UserPasswordInfoRepository {

  override val tableName: String = "user_password_info"

  override def update(e: UserPasswordInfo)(implicit session: Session): Int =
    (sqlu"update #$tableName set password=" +? e.password
      ).executeUpdate()

  override def insert(e: UserPasswordInfo)(implicit session: Session): Long =
    (sqlu"insert into #$tableName (hasher, password, salt, user_login_info_id) values (" +? e.hasher + "," +?
      e.password + "," +? e.salt + "," +? e.userLoginInfoId + ")"
      ).executeInsert()

  override def findByUserLoginId(userLoginInfoId: Long)
    (implicit session: Session): Option[UserPasswordInfo] = {

    sql"select * from #$tableName where user_login_info_id = $userLoginInfoId".as[UserPasswordInfo].firstOption
  }

}
