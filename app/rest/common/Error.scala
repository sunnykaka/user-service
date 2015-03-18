package rest.common

import play.api.http.{ContentTypes, ContentTypeOf, Writeable}
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc.{RequestHeader, Codec, Request}
import utils.common.error.ErrorInfo
import utils.common.http.CommonHeader

/**
 * Created by liubin on 15-3-17.
 */
case class Error(
  code: String,
  message: String,
  requestId: Option[String],
  resource: Option[String]) {

}

object Error {

  implicit val errorFormat = (
    (JsPath \ "code").format[String] and
      (JsPath \ "message").format[String] and
      (JsPath \ "requestId").formatNullable[String] and
      (JsPath \ "resource").formatNullable[String]
    )(Error.apply(_, _, _, _), unlift(Error.unapply))

  /**
   * `ContentType` for `Error` values
   */
  implicit def contentTypeOf_Error(implicit codec: Codec): ContentTypeOf[Error] = {
    ContentTypeOf[Error](Some(ContentTypes.JSON))
  }

  /**
   * `Writeable` for `Error` values
   */
  implicit def writeableOf_Error(implicit codec: Codec): Writeable[Error] = {
    Writeable(content => codec.encode(Json.toJson(content).toString()))
  }

  def apply(errorInfo: ErrorInfo, message: String)(implicit request: RequestHeader): Error = {
    Error(errorInfo.code, message, CommonHeader.requestId(), Some(request.uri))
  }

  def apply(errorInfo: ErrorInfo)(implicit request: RequestHeader): Error = {
    Error(errorInfo.code, errorInfo.message, CommonHeader.requestId(), Some(request.uri))
  }


}
