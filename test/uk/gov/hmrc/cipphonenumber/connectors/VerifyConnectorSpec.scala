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

package uk.gov.hmrc.cipphonenumber.connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.Status.OK
import play.api.libs.json.Json
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.cipphonenumber.config.{AppConfig, CipVerificationConfig, CircuitBreakerConfig}
import uk.gov.hmrc.cipphonenumber.utils.TestActorSystem
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.test.{HttpClientV2Support, WireMockSupport}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt

class VerifyConnectorSpec
    extends AnyWordSpec
    with Matchers
    with WireMockSupport
    with ScalaFutures
    with HttpClientV2Support
    with TestActorSystem
    with MockitoSugar {

  "verify" should {
    val url: String = "/phone-number/verify"

    "delegate to http client" in new SetUp {
      stubFor(
        post(urlEqualTo(url))
          .willReturn(aResponse().withBody("""{"res":"res"}"""))
      )

      private val result = await(verifyConnector.verify(Json.parse(s"""{"req": "req"}""")))

      result.status shouldBe OK
      result.json shouldBe Json.parse("""{"res":"res"}""")

      verify(
        postRequestedFor(urlEqualTo(url))
          .withRequestBody(equalToJson(s"""{"req": "req"}"""))
      )
    }
  }

  "status" should {
    val url: String = "/phone-number/notifications/%s"

    "delegate to http client" in new SetUp {
      val notificationId = "test-notification-id"

      stubFor(
        get(urlEqualTo(url.format(notificationId)))
          .willReturn(aResponse().withBody("""{"res":"res"}"""))
      )

      private val result = await(verifyConnector.status(notificationId))

      result.status shouldBe OK
      result.json shouldBe Json.parse("""{"res":"res"}""")

      verify(
        getRequestedFor(urlEqualTo(url.format(notificationId)))
      )
    }
  }

  "verifyPasscode" should {
    val url: String = "/phone-number/verify/passcode"

    "delegate to http client" in new SetUp {
      stubFor(
        post(urlEqualTo(url))
          .willReturn(aResponse().withBody("""{"res":"res"}"""))
      )

      private val result = await(verifyConnector.verifyPasscode(Json.parse(s"""{"req": "req"}""".stripMargin)))

      result.status shouldBe OK
      result.json shouldBe Json.parse("""{"res":"res"}""")

      verify(
        postRequestedFor(urlEqualTo(url))
          .withRequestBody(equalToJson(s"""{"req": "req"}""".stripMargin))
      )
    }
  }

  trait SetUp {
    implicit protected val hc: HeaderCarrier = HeaderCarrier()

    protected val appConfigMock: AppConfig                         = mock[AppConfig]
    protected val cipVerificationConfigMock: CipVerificationConfig = mock[CipVerificationConfig]

    protected val cbConfigData: CircuitBreakerConfig =
      CircuitBreakerConfig("", 5, 5.minutes, 30.seconds, 5.minutes, 1, 0)

    when(appConfigMock.verificationConfig).thenReturn(cipVerificationConfigMock)
    when(cipVerificationConfigMock.cbConfig).thenReturn(cbConfigData)
    when(cipVerificationConfigMock.url).thenReturn(wireMockUrl)

    val verifyConnector = new VerifyConnector(
      httpClientV2,
      appConfigMock
    )
  }
}
