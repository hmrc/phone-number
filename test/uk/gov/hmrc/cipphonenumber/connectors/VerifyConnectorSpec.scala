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
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.Configuration
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.Json
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout, status}
import uk.gov.hmrc.cipphonenumber.config.AppConfig
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.test.{HttpClientV2Support, WireMockSupport}

import scala.concurrent.ExecutionContext.Implicits.global

class VerifyConnectorSpec extends AnyWordSpec
  with Matchers
  with WireMockSupport
  with ScalaFutures
  with HttpClientV2Support {

  "verify" should {
    val url: String = "/customer-insight-platform/phone-number/verify"

    "return HttpResponse OK when upstream returns 200" in new Setup {
      val phoneNumber = "07843274323"

      stubFor(
        post(urlEqualTo(url))
          .willReturn(aResponse().withBody("""{"m":"m"}""")
          )
      )

      val result = verifyConnector.verify(Json.parse(s"""{"phoneNumber": "$phoneNumber"}"""))

      status(result) shouldBe OK
      contentAsJson(result) shouldBe Json.parse("""{"m":"m"}""")

      verify(
        postRequestedFor(urlEqualTo(url))
          .withRequestBody(equalToJson(s"""{"phoneNumber": "$phoneNumber"}"""))
      )
    }

    "return HttpResponse BAD_REQUEST when upstream returns 400" in new Setup {
      val phoneNumber = "07843274323"

      stubFor(
        post(urlEqualTo(url))
          .willReturn(badRequest().withBody("""{"message": "invalid"}"""))
      )

      val result = verifyConnector.verify(Json.parse(s"""{"phoneNumber": "$phoneNumber"}"""))

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.parse("""{"message": "invalid"}""")

      verify(
        postRequestedFor(urlEqualTo(url))
          .withRequestBody(equalToJson(s"""{"phoneNumber": "$phoneNumber"}"""))
      )
    }

    "return HttpResponse INTERNAL_SERVER_ERROR when upstream returns 500" in new Setup {
      val phoneNumber = "07843274323"

      stubFor(
        post(urlEqualTo(url))
          .willReturn(serverError)
      )

      val result = verifyConnector.verify(Json.parse(s"""{"phoneNumber": "$phoneNumber"}"""))

      status(result) shouldBe INTERNAL_SERVER_ERROR

      verify(
        postRequestedFor(urlEqualTo(url))
          .withRequestBody(equalToJson(s"""{"phoneNumber": "$phoneNumber"}"""))
      )
    }
  }

  "verifyOtp" should {
    val url: String = "/customer-insight-platform/phone-number/verify/otp"

    "return HttpResponse OK when upstream returns 200" in new Setup {
      val phoneNumber = "07843274323"
      val passcode = "123456"

      stubFor(
        post(urlEqualTo(url))
          .willReturn(aResponse().withBody("""{"m":"m"}""")
          )
      )

      val result = verifyConnector.verifyOtp(Json.parse(
        s"""{
              "phoneNumber": "$phoneNumber",
              "passcode": "$passcode"
            }""".stripMargin))

      status(result) shouldBe OK
      contentAsJson(result) shouldBe Json.parse("""{"m":"m"}""")

      verify(
        postRequestedFor(urlEqualTo(url))
          .withRequestBody(equalToJson(
            s"""{
                  "phoneNumber": "$phoneNumber",
                  "passcode": "$passcode"
                }""".stripMargin))
      )
    }

    "return HttpResponse BAD_REQUEST when upstream returns 400" in new Setup {
      val phoneNumber = "07843274323"
      val passcode = "123456"

      stubFor(
        post(urlEqualTo(url))
          .willReturn(badRequest().withBody("""{"message": "invalid"}"""))
      )

      val result = verifyConnector.verifyOtp(Json.parse(
        s"""{
              "phoneNumber": "$phoneNumber",
              "passcode": "$passcode"
            }""".stripMargin))

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.parse("""{"message": "invalid"}""")

      verify(
        postRequestedFor(urlEqualTo(url))
          .withRequestBody(equalToJson(
            s"""{
                  "phoneNumber": "$phoneNumber",
                  "passcode": "$passcode"
                }""".stripMargin))
      )
    }

    "return HttpResponse INTERNAL_SERVER_ERROR when upstream returns 500" in new Setup {
      val phoneNumber = "07843274323"
      val passcode = "123456"

      stubFor(
        post(urlEqualTo(url))
          .willReturn(serverError)
      )

      val result = verifyConnector.verifyOtp(Json.parse(
        s"""{
              "phoneNumber": "$phoneNumber",
              "passcode": "$passcode"
            }""".stripMargin))

      status(result) shouldBe INTERNAL_SERVER_ERROR

      verify(
        postRequestedFor(urlEqualTo(url))
          .withRequestBody(equalToJson(
            s"""{
                  "phoneNumber": "$phoneNumber",
                  "passcode": "$passcode"
                }""".stripMargin))
      )
    }
  }

  trait Setup {

    implicit val hc: HeaderCarrier = HeaderCarrier()

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
