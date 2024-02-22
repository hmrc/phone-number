/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.cipphonenumber

import com.github.tomakehurst.wiremock.client.WireMock._
import org.apache.pekko.http.scaladsl.model.MediaTypes
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.http.HeaderNames
import play.api.http.Status._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient
import play.api.libs.ws.ahc.AhcCurlRequestLogger
import play.libs.Json
import uk.gov.hmrc.http.test.ExternalWireMockSupport

class VerificationControllerIntegrationSpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with GuiceOneServerPerSuite
    with ExternalWireMockSupport
    with MockitoSugar {

  private val wsClient = app.injector.instanceOf[WSClient]
  private val baseUrl  = s"http://localhost:$port/phone-number"

  override def fakeApplication(): Application =
    GuiceApplicationBuilder()
      .configure("microservice.services.cipphonenumber.verification.port" -> externalWireMockPort)
      .build()

  "VerificationController" should {
    "respond with OK status" when {
      "an valid json payload is provided" in {
        externalWireMockServer.stubFor(
          post(urlEqualTo("/phone-number/verify"))
            .withRequestBody(equalToJson("""{"phoneNumber":"07123123123"}"""))
            .withHeader(HeaderNames.CONTENT_TYPE, equalTo(MediaTypes.`application/json`.value))
            .willReturn(
              aResponse()
                .withJsonBody(Json.parse("""{"status":"VERIFIED", "code": "Phone verification code successfully sent"}"""))
                .withStatus(OK)
            )
        )

        val response =
          wsClient
            .url(s"$baseUrl/verify")
            .withHttpHeaders(
              HeaderNames.AUTHORIZATION -> "local-test-token",
              HeaderNames.CONTENT_TYPE  -> MediaTypes.`application/json`.value
            )
            .withRequestFilter(AhcCurlRequestLogger())
            .post("""{"phoneNumber": "07123123123"}""")
            .futureValue

        response.status shouldBe OK
        Json.parse(response.body) shouldBe Json.parse("""{"status":"VERIFIED", "code": "Phone verification code successfully sent"}""")
      }
    }

    "respond with BAD_REQUEST status" when {
      "an invalid json payload is provided" in {
        val response =
          wsClient
            .url(s"$baseUrl/verify")
            .withHttpHeaders(
              HeaderNames.AUTHORIZATION -> "local-test-token",
              HeaderNames.CONTENT_TYPE  -> MediaTypes.`application/json`.value
            )
            .withRequestFilter(AhcCurlRequestLogger())
            .post("{")
            .futureValue

        response.status shouldBe BAD_REQUEST
      }
    }
  }
}
