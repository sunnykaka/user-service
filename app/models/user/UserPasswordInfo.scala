package models.user

import models.common.Entity

import scala.slick.jdbc.{GetResult => GR}

/**
 * Created by liubin on 15-3-10.
 */
case class UserPasswordInfo(
  id: Option[Long] = None,
  hasher: String = "",
  password: String = "",
  salt: Option[String] = None,
  userLoginInfoId: Long) extends Entity

object UserPasswordInfo {
  implicit val GetResultUserPasswordInfo = GR { r =>
    import r._
    UserPasswordInfo(id = <<?[Long], hasher = <<[String], password = <<[String],
      salt = <<?[String], userLoginInfoId = <<[Long])
  }

}