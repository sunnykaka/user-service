package repositories.user.impl

import repositories.user.UserRepository

import java.sql.{Timestamp, SQLException}

import models.user.User
import org.joda.time.DateTime

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.jdbc.{GetResult => GR, SetParameter => SP, StaticQuery}
import repositories.common.SlickImplicits._


import scala.slick.jdbc.StaticQuery.interpolation


/**
 * Created by liubin on 15-3-11.
 */
class UserRepositoryImpl extends UserRepository{

  override val tableName: String = "user"

  override def update(u: User)(implicit session: Session): Int = (
    sqlu"update #$tableName set username=" +? u.username + ", email=" +? u.email + ", gender=" +? u.gender +
      ", birthday=" +? u.birthday + " where id =" +? u.id
    ).executeUpdate()


  override def insert(u: User)(implicit session: Session): Long = (
    sqlu"insert into #$tableName values (null, " +? u.username + "," +? u.email + "," +? u.gender +
      "," +? u.birthday + ")"
    ).executeInsert()

  override def findByUsername(username: String)(implicit session: Session): Option[User] = {
    sql"select * from #$tableName where username = $username".as[User].firstOption
  }

  override def findByEmail(email: String)(implicit session: Session): Option[User] = {
    sql"select * from #$tableName where email = $email".as[User].firstOption
  }

}
