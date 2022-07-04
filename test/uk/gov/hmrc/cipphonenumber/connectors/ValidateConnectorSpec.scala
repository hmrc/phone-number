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
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Configuration
import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.libs.json.Json
import play.api.test.Helpers.{defaultAwaitTimeout, status}
import uk.gov.hmrc.cipphonenumber.config.AppConfig
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.test.{HttpClientV2Support, WireMockSupport}

import scala.concurrent.ExecutionContext.Implicits.global

class ValidateConnectorSpec extends AnyWordSpec
  with Matchers
  with WireMockSupport
  with GuiceOneAppPerSuite
  with ScalaFutures
  with HttpClientV2Support {

  val url: String = "/customer-insight-platform/phone-number/validate-format"

  "ValidatorConnector.callService" should {
    "return HttpResponse OK for valid input" in new Setup {
      val phoneNumber = "07843274323"
      stubFor(
        post(urlEqualTo(url))
          .willReturn(aResponse())
      )

      val result = validateConnector.callService(Json.parse(s"""{"phoneNumber" : "$phoneNumber"}"""))
      status(result) shouldBe OK

      verify(
        postRequestedFor(urlEqualTo(url))
          .withRequestBody(equalToJson(s"""{"phoneNumber": "07843274323"}"""))
      )
    }

    "return HttpResponse BAD_REQUEST for invalid input" in new Setup {
      val phoneNumber = "07843274323"
      stubFor(
        post(urlEqualTo(url))
          .willReturn(badRequest().withBody(s"""{"message": "invalid"}"""))
      )

      val result = validateConnector.callService(Json.parse(s"""{"phoneNumber" : "$phoneNumber"}"""))
      status(result) shouldBe BAD_REQUEST

      verify(
        postRequestedFor(urlEqualTo(url))
          .withRequestBody(equalToJson(s"""{"phoneNumber": "07843274323"}"""))
      )
    }
  }

  trait Setup {

    implicit val hc: HeaderCarrier = HeaderCarrier()

    private val appConfig = new AppConfig(
      Configuration.from(Map(
        "microservice.services.cipphonenumber.validation.host" -> wireMockHost,
        "microservice.services.cipphonenumber.validation.port" -> wireMockPort,
        "microservice.services.cipphonenumber.validation.protocol" -> "http",
      ))
    )

    val validateConnector = new ValidateConnector(
      httpClientV2,
      appConfig
    )
  }
}
