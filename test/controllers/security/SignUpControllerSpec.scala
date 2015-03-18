package controllers.security

import org.scalatestplus.play._
import play.api.libs.json._
import play.api.test.Helpers._
import play.api.test._
import rest.models.{Token, SignUp}

import utils.common.RandomUtils._
import utils.error.UserErrors

class SignUpControllerSpec extends PlaySpec with OneAppPerSuite {

  "SignUpController" must {

    "send 404 on a bad request" in {
      route(FakeRequest(GET, "/boum")) mustBe None
    }

    "render the index page" in {
      val home = route(FakeRequest(GET, "/")).get

      status(home) mustBe OK
      contentType(home) must contain ("application/json")

    }

    val identifier = randomAlpString(8)

    "用户成功注册" in {

      val request = FakeRequest(POST, routes.SignUpController.signUp().url).
        withJsonBody(Json.toJson(
          SignUp(password = "111111", identifier = identifier, username = randomAlpString(8))
        ))

      val response = route(request).get
      status(response) mustBe OK
      contentType(response) must contain ("application/json")
      contentAsJson(response).validate[Token].fold({
        errors =>
          throw new AssertionError(JsError.toFlatJson(errors))
      }, {
        token =>
          token.token must not be null
      })

    }

    "用户注册失败,用户名重复" in {

      val request = FakeRequest(POST, routes.SignUpController.signUp().url).
        withJsonBody(Json.toJson(
          SignUp(password = "111111", identifier = identifier, username = randomAlpString(8))
        ))

      val response = route(request).get
      status(response) mustBe CONFLICT
      contentType(response) must contain ("application/json")
      val responseJson = contentAsJson(response)
      (responseJson \ "code").as[String] mustBe UserErrors.UserAlreadyExists.code
      (responseJson \ "message").as[String] mustBe UserErrors.UserAlreadyExists.message
      (responseJson \ "resource").as[String] must not be empty

    }


  }
}
