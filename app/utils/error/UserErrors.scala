package utils.error

import utils.common.error.ErrorInfo


object UserErrors {

  val CredentialsNotCorrect = ErrorInfo("CredentialsNotCorrect", "用户名或密码错误")

  val UserAlreadyExists = ErrorInfo("UserAlreadyExists", "用户已存在")

}
