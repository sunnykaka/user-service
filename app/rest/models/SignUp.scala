package rest.models

import play.api.libs.json.Json

/**
 * Case class for signUp element
 */
case class SignUp(
  password: String,
  identifier: String,
  username: String)

object SignUp {
  implicit val signUpFormat = Json.format[SignUp]
}