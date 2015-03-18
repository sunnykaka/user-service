package repositories.user

import models.user.{User, UserLoginInfo}
import play.api.db.slick.Session
import repositories.common.BaseRepository


import scala.concurrent.Future

/**
 * Created by liubin on 15-3-12.
 */
trait UserLoginInfoRepository extends BaseRepository[UserLoginInfo]{
  def findByProviderIdKey(providerId: String, providerKey: String)(implicit session: Session): Option[UserLoginInfo]
}
