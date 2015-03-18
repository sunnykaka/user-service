package models.user

import models.common.Entity

import scala.slick.jdbc.{GetResult => GR}

case class UserLoginInfo(
  id: Option[Long] = None,
  providerId: String = "",
  providerKey: String = "",
  userId: Long) extends Entity

object UserLoginInfo {

  implicit val GetResultUserLoginInfo = GR { r =>
    import r._
    UserLoginInfo(id = <<?[Long], providerId = <<[String], providerKey = <<[String],
      userId = <<[Long])
  }
}

