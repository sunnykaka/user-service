package utils.error

import utils.common.error.ErrorInfo


object UserErrors {

  val CredentialsNotCorrect = ErrorInfo("CredentialsNotCorrect", "用户名或密码错误")

  val UserAlreadyExists = ErrorInfo("UserAlreadyExists", "用户已存在")

  val UserUnauthorized = ErrorInfo("UserUnauthorized", "用户未登录")

  val UserForbidden = ErrorInfo("UserForbidden", "用户权限不足")

  val IdentityNotFound = ErrorInfo("IdentityNotFound", "账号不存在")

  val LoginFailed = ErrorInfo("LoginFailed", "登录失败")

  val RequestError = ErrorInfo("UserRequestError", "用户请求的时候发生异常")

}
