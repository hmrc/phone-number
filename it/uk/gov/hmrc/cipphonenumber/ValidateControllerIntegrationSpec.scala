
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

package uk.gov.hmrc.cipphonenumber

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSResponse}
import uk.gov.hmrc.http.test.WireMockSupport

class ValidateControllerIntegrationSpec extends AnyWordSpec with Matchers with GuiceOneServerPerSuite
  with ScalaFutures
  with IntegrationPatience
  with WireMockSupport{

  "Calling / Validate" should {
    "return header validation response" when {
      "phone number is correct" in {
        wireMockServer.stubFor(WireMock.post("/customer-insight-platform/phone-number/validate-format").willReturn {
          ok()
        })

        val response: WSResponse = wsClient
          .url(resource("/customer-insight-platform/phone-number/validate"))
          .withHttpHeaders("content-type" -> "application/json")
          .post(Json.parse {
            """
              {
                "phoneNumber" : "07890234567"
              }
              """.stripMargin
          })
          .futureValue

        response.status shouldBe 200
        wireMockServer.verify(
          postRequestedFor(urlEqualTo("/customer-insight-platform/phone-number/validate-format"))
            .withHeader("content-type", equalTo("application/json"))
        )

      }
    }
  }
  private lazy val wsClient: WSClient = app.injector.instanceOf[WSClient]

  private def resource(path: String) = s"http://localhost:$port$path"

  override lazy val app: Application = new GuiceApplicationBuilder()
    .configure("microservice.services.cipphonenumber.validation.port" -> wireMockPort)
    .build()
}

