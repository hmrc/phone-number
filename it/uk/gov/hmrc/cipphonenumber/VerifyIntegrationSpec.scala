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

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.json.Json
import play.api.libs.ws.ahc.AhcCurlRequestLogger
import play.api.libs.ws.{WSClient, writeableOf_JsValue}

class VerifyIntegrationSpec
  extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with GuiceOneServerPerSuite {

  private val wsClient = app.injector.instanceOf[WSClient]
  private val baseUrl = s"http://localhost:$port"

  "/verify" should {
    "respond with 200 with valid telephone number" in {
      val response =
        wsClient
          .url(s"$baseUrl/customer-insight-platform/phone-number/verify")
          .withHttpHeaders(("Authorization", "fake-token"))
          .withRequestFilter(AhcCurlRequestLogger())
          .post(Json.parse("""{"phoneNumber" : "07843274323"}"""))
          .futureValue

      response.status shouldBe 202
    }

    "respond with 400 status for invalid request" in {
      val response =
        wsClient
          .url(s"$baseUrl/customer-insight-platform/phone-number/verify")
          .withHttpHeaders(("Authorization", "fake-token"))
          .withRequestFilter(AhcCurlRequestLogger())
          .post(Json.parse("""{"phoneNumber" : "aaaa"}"""))
          .futureValue

      response.status shouldBe 400
      (response.json \ "code").as[Int] shouldBe 1002
      (response.json \ "message").as[String] shouldBe "Enter a valid telephone number"
    }
  }
}
