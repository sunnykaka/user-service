package controllers

import com.mohiva.play.silhouette.api.util.Credentials
import play.api.libs.json.Json

/**
 * Created by liubin on 15-3-17.
 */
package object security {

  implicit val restCredentialFormat = Json.format[Credentials]

}
