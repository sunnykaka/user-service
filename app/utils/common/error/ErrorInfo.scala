package utils.common.error

/**
  * Created by liubin on 15-3-17.
  */

case class ErrorInfo(
  code: String,
  message: String)

object ErrorInfo {

  val JsonFormatError = ErrorInfo("JsonFormatError", "Json格式错误")

  val InternalServerError = ErrorInfo("InternalServerError", "服务器内部发生错误")

  val ActionNotFound = ErrorInfo("ActionNotFound", "请求的Action不存在")

}
