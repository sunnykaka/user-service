package utils.di

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import repositories.user.{UserPasswordInfoRepository, UserLoginInfoRepository, UserRepository}
import repositories.user.impl.{UserPasswordInfoRepositoryImpl, UserLoginInfoRepositoryImpl, UserRepositoryImpl}
import services.user.UserService

/**
 * Created by liubin on 15-3-12.
 */
class UserModule extends AbstractModule with ScalaModule  {

  def configure() {
//    bind[UserRepository].to[UserRepositoryImpl]
    bind[UserLoginInfoRepository].to[UserLoginInfoRepositoryImpl]
    bind[UserPasswordInfoRepository].to[UserPasswordInfoRepositoryImpl]
//    bind[UserService].to[UserService]
  }


}
