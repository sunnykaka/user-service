package repositories.user

import java.sql.{Timestamp, SQLException}

import models.user.User
import org.joda.time.DateTime
import play.api.db.slick.Session
import repositories.common.BaseRepository

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.jdbc.{GetResult => GR, SetParameter => SP, StaticQuery}
import repositories.common.SlickImplicits._

import scala.slick.jdbc.StaticQuery.interpolation


/**
 * Created by liubin on 15-3-10.
 */
trait UserRepository extends BaseRepository[User] {

  def findByUsername(username: String)(implicit session: Session): Option[User]

  def findByEmail(email: String)(implicit session: Session): Option[User]

}

