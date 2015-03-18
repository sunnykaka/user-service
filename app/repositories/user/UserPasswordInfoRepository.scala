package repositories.user

import models.user.{UserLoginInfo, UserPasswordInfo}
import play.api.db.slick.Session
import repositories.common.BaseRepository

/**
 * Created by liubin on 15-3-12.
 */
trait UserPasswordInfoRepository extends BaseRepository[UserPasswordInfo]{

  def findByUserLoginId(userLoginInfoId: Long)(implicit session: Session): Option[UserPasswordInfo]

}
