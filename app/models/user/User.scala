package models.user

import com.mohiva.play.silhouette.api.{LoginInfo, Identity}
import org.joda.time.DateTime
import models.common.Entity
import play.api.libs.json.Json

import repositories.common.SlickImplicits._
import rest.models.SignUp
import utils.common.json.EnumUtils
import scala.slick.jdbc.{GetResult => GR, SetParameter => SP, StaticQuery}


/**
 * Created by liubin on 15-3-10.
 */
case class User(
  id: Option[Long] = None,
  username: Option[String] = None,
  email: Option[String] = None,
  gender: Option[User.Gender.Value] = Some(User.Gender.Unknown),
  birthday: Option[DateTime] = None) extends Entity with Identity

object User{

  object Gender extends Enumeration() {
    type Gender = Value
    val Male = Value("Male")
    val Female = Value("Female")
    val Unknown = Value("Unknown")

    implicit val enumAFormat = EnumUtils.enumFormat(Gender)
  }

  //slick
  implicit val getGenderResult = GR { r => r.nextStringOption().map(User.Gender.withName) }

  implicit val setGenderParameter = SP { (v: Option[User.Gender.Value], pp) => pp.setStringOption(v.map(_.toString)) }

  implicit val GetResultUser = GR { r =>
    import r._
    //    User(<<?[Long], <<?[String], <<?[String], <<?[String].map(User.Gender.withName), <<?[java.sql.Date].map(new DateTime(_)))
    User(id = <<?[Long], username = <<?[String], email = <<?[String],
      gender = <<?[User.Gender.Value], birthday = <<?[DateTime])
  }

  //json
  implicit val jsonFormat = Json.format[User]
}
