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
      val phoneNumber = "07843274323"
      stubFor(
        post(urlEqualTo(url))
          .willReturn(aResponse().withBody("""{"m":"m"}""")
          )
      )

      val actual = verifyConnector.callService(Json.parse(s"""{"phoneNumber" : "$phoneNumber"}"""))

      status(actual) shouldBe OK
      verify(
        postRequestedFor(urlEqualTo(url))
          .withRequestBody(equalToJson(s"""{"phoneNumber": "$phoneNumber"}"""))
      )
    }

    "return HttpResponse BAD_REQUEST for invalid input" in new Setup {
      val phoneNumber = "07843274323"
      stubFor(
        post(urlEqualTo(url))
          .willReturn(badRequest().withBody(s"""{"message": "invalid"}"""))
      )

      val result = verifyConnector.callService(Json.parse(s"""{"phoneNumber" : "$phoneNumber"}"""))
      status(result) shouldBe BAD_REQUEST

      verify(
        postRequestedFor(urlEqualTo(url))
          .withRequestBody(equalToJson(s"""{"phoneNumber": "$phoneNumber"}"""))
      )
    }
  }

  trait Setup {

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
