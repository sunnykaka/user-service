package repositories.user.impl

import models.user.{User, UserLoginInfo}
import repositories.user.UserLoginInfoRepository

import scala.slick.driver.MySQLDriver.simple._

import scala.slick.jdbc.{GetResult => GR, SetParameter => SP, StaticQuery}
import repositories.common.SlickImplicits._

import scala.slick.jdbc.StaticQuery.interpolation


/**
 * Created by liubin on 15-3-12.
 */
class UserLoginInfoRepositoryImpl extends UserLoginInfoRepository{

  override val tableName: String = "user_login_info"

  override def update(li: UserLoginInfo)(implicit session: Session): Int =
    (sqlu"update #$tableName set provider_id=" +? li.providerId + ", provider_key=" +? li.providerKey + " where id =" +? li.id
    ).executeUpdate()


  override def insert(li: UserLoginInfo)(implicit session: Session): Long =
    (sqlu"insert into #$tableName (provider_id, provider_key, user_id) values (" +? li.providerId + "," +?
      li.providerKey + "," +? li.userId + ")"
    ).executeInsert()

  override def findByProviderIdKey(providerId: String, providerKey: String)
    (implicit session: Session): Option[UserLoginInfo] = {

    sql"select * from #$tableName where provider_id = $providerId and provider_key = $providerKey".as[UserLoginInfo].firstOption
  }

}
