/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.cipphonenumber.connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.http.Fault
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.Configuration
import play.api.http.Status
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.{JsValue, Json}
import play.api.test.Helpers.{contentAsJson, contentAsString, defaultAwaitTimeout, status}
import play.shaded.ahc.org.asynchttpclient.exception.RemotelyClosedException
import uk.gov.hmrc.cipphonenumber.config.AppConfig
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.test.{HttpClientV2Support, WireMockSupport}

import scala.concurrent.ExecutionContext.Implicits.global

class VerifyConnectorSpec extends AnyWordSpec
  with Matchers
  with WireMockSupport
  with ScalaFutures
  with HttpClientV2Support
{

  implicit val hc = HeaderCarrier()
  val url: String = "/customer-insight-platform/phone-number/verify"

  "VerifyConnector.callService" should {

    "return HttpResponse OK and verified for valid input and verification passed" in new Setup {
      stubFor(
        post(urlEqualTo(url)).withRequestBody(equalToJson(passcodeRequestJsonString))
          .willReturn(aResponse()
            .withStatus(Status.OK)
            .withBody(validPasscodeResponseJsonString)
          )
      )

      val actual = verifyConnector.callService(requestJson)

      status(actual) shouldBe OK
      contentAsJson(actual) shouldBe expectedValidPasscodeResponseJson
      verify(
        postRequestedFor(urlEqualTo(url))
          .withRequestBody(equalToJson(expectedToCallServiceWith))
      )
    }

    "return HttpResponse OK and verification error for valid input and verification failed" in new Setup {
      stubFor(
        post(urlEqualTo(url)).withRequestBody(equalToJson(passcodeRequestJsonString))
          .willReturn(aResponse()
            .withStatus(Status.OK)
            .withBody(invalidPasscodeResponseJsonString)
          )
      )

      val actual = verifyConnector.callService(requestJson)

      status(actual) shouldBe OK
      contentAsJson(actual) shouldBe expectedInvalidPasscodeResponseJson
      verify(
        postRequestedFor(urlEqualTo(url))
          .withRequestBody(equalToJson(expectedToCallServiceWith))
      )
    }

    "return HttpResponse BadRequest and validation error for invalid input" in new Setup {
      stubFor(
        post(urlEqualTo(url)).withRequestBody(equalToJson(passcodeRequestJsonString))
          .willReturn(aResponse()
            .withStatus(Status.BAD_REQUEST)
            .withBody(invalidPhoneNumberResponseJsonString)
          )
      )

      val actual = verifyConnector.callService(requestJson)

      status(actual) shouldBe BAD_REQUEST
      contentAsJson(actual) shouldBe expectedInvalidPhoneNumberResponseJson
      verify(
        postRequestedFor(urlEqualTo(url))
          .withRequestBody(equalToJson(expectedToCallServiceWith))
      )
    }

    "return HttpResponse InternalServerError and for 5xx responses" in new Setup {
      stubFor(
        post(urlEqualTo(url)).withRequestBody(equalToJson(passcodeRequestJsonString))
          .willReturn(aResponse()
            .withStatus(Status.INTERNAL_SERVER_ERROR)
          )
      )

      val actual = verifyConnector.callService(requestJson)

      status(actual) shouldBe INTERNAL_SERVER_ERROR
      contentAsString(actual) shouldBe empty
      verify(
        postRequestedFor(urlEqualTo(url))
          .withRequestBody(equalToJson(expectedToCallServiceWith))
      )
    }

    "return Failure for technical exception" in new Setup {
      stubFor(
        post(urlEqualTo(url)).withRequestBody(equalToJson(passcodeRequestJsonString))
          .willReturn(aResponse()
            .withFault(Fault.MALFORMED_RESPONSE_CHUNK)
          )
      )

      val actual = verifyConnector.callService(requestJson)

      actual.failed.futureValue shouldBe an[RemotelyClosedException]
      verify(
        postRequestedFor(urlEqualTo(url))
          .withRequestBody(equalToJson(expectedToCallServiceWith))
      )
    }

  }

  trait Setup {

    val phoneNumber = "07843274323"
    val passcode = "ABCDEF"
    val passcodeRequestJsonString: String = s"""{"phoneNumber" : "$phoneNumber", "passcode" : "$passcode"}"""
    val requestJson: JsValue = Json.parse(passcodeRequestJsonString)

    val validPasscodeResponseJsonString: String = s"""{"message": "Verified"}"""
    val expectedValidPasscodeResponseJson: JsValue = Json.obj("message"->Json.toJson("Verified"))

    val invalidPasscodeResponseJsonString: String = s"""{"code": "VERIFICATION_MESSAGE", "message": "Failed Verification"}"""
    val expectedInvalidPasscodeResponseJson: JsValue = Json.obj("code"->Json.toJson("VERIFICATION_MESSAGE"), "message"->Json.toJson("Failed Verification"))

    val invalidPhoneNumberResponseJsonString: String = s"""{"code": "VALIDATION_ERROR", "message": "Enter a valid telephone number"}"""
    val expectedInvalidPhoneNumberResponseJson: JsValue =
      Json.obj("code"->Json.toJson("VALIDATION_ERROR"),
        "message"->Json.toJson("Enter a valid telephone number"))

    val expectedToCallServiceWith: String = s"""{"phoneNumber": "07843274323", "passcode": "ABCDEF"}"""

    private val appConfig = new AppConfig(
      Configuration.from(Map(
        "microservice.services.cipphonenumber.verification.host" -> wireMockHost,
        "microservice.services.cipphonenumber.verification.port" -> wireMockPort,
        "microservice.services.cipphonenumber.verification.protocol" -> "http"
      ))
    )

    val verifyConnector = new VerifyConnector(
      httpClientV2,
      appConfig
    )

  }
}
