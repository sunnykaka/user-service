package utils.common.http

import play.api.mvc.RequestHeader

/**
 * Created by liubin on 15-3-17.
 */
object CommonHeader {

  val RequestIdHeadKey: String = "x-boobee-request-id"

  def requestId[A]()(implicit request: RequestHeader): Option[String] = {
    request.headers.get(RequestIdHeadKey)
  }

}
