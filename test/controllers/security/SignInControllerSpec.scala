package controllers.security

import com.mohiva.play.silhouette.api.util.Credentials
import org.scalatestplus.play._
import play.api.libs.json.{JsError, Json}
import play.api.test.Helpers._
import play.api.test._
import rest.models.{SignUp, Token}
import utils.common.RandomUtils._
import play.api.libs.json._

import controllers.index.routes.ApplicationController
import utils.error.UserErrors

class SignInControllerSpec extends PlaySpec with OneAppPerSuite {

  "SignInController" must {

    val identifier = randomAlpString(8)
    val username = randomAlpString(8)

    "用户成功登录" in {

      val request = FakeRequest(POST, routes.SignUpController.signUp().url).
        withJsonBody(Json.toJson(
          SignUp(password = "111111", identifier = identifier, username = username)
        ))
      val response = route(request).get
      status(response) mustBe OK

      val signInRequest = FakeRequest(POST, routes.SignInController.signIn().url).
        withJsonBody(Json.toJson(
        Credentials(password = "111111", identifier = identifier)
      ))
      val signInResponse = route(signInRequest).get
      status(signInResponse) mustBe OK
      contentType(signInResponse) must contain ("application/json")
      contentAsJson(signInResponse).validate[Token].fold({
        errors =>
          throw new AssertionError(JsError.toFlatJson(errors))
      }, {
        token =>
          token.token must not be null
          //能够成功访问首页
          val indexRequest = FakeRequest(GET, ApplicationController.index().url).
            withHeaders("X-Auth-Token" -> token.token)
          val indexResponse = route(indexRequest).get
          status(indexResponse) mustBe OK
          contentType(indexResponse) must contain ("application/json")
          contentAsString(indexResponse) must include ("username")
      })

    }

    "用户登录失败,用户不存在" in {

      val request = FakeRequest(POST, routes.SignInController.signIn().url).
        withJsonBody(Json.toJson(
        Credentials(password = "111111", identifier = randomAlpString(8))
      ))
      val response = route(request).get
      status(response) mustBe UNAUTHORIZED
      contentType(response) must contain ("application/json")
      val responseJson = contentAsJson(response)
      println(responseJson)

      (responseJson \ "code").as[String] mustBe UserErrors.CredentialsNotCorrect.code
      (responseJson \ "message").as[String] mustBe UserErrors.CredentialsNotCorrect.message
      (responseJson \ "resource").as[String] must not be empty


    }

    "用户登录失败,用户密码错误" in {

      val request = FakeRequest(POST, routes.SignInController.signIn().url).
        withJsonBody(Json.toJson(
        Credentials(password = "ddffss", identifier = identifier)
      ))
      val response = route(request).get
      status(response) mustBe UNAUTHORIZED
      contentType(response) must contain ("application/json")
      val responseJson = contentAsJson(response)

      (responseJson \ "code").as[String] mustBe UserErrors.CredentialsNotCorrect.code
      (responseJson \ "message").as[String] mustBe UserErrors.CredentialsNotCorrect.message
      (responseJson \ "resource").as[String] must not be empty

    }

  }
}
