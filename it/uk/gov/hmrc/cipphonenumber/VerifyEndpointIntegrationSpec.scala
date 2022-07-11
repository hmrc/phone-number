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

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, writeableOf_JsValue}

class VerifyEndpointIntegrationSpec
  extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with GuiceOneServerPerSuite {

  private val wsClient = app.injector.instanceOf[WSClient]
  private val baseUrl = s"http://localhost:$port"

  "verify details endpoint" should {
    "respond with 200 status when data is valid and result of verification is verified" in {
      val response =
        wsClient
          .url(s"$baseUrl/customer-insight-platform/phone-number/verify")
          .post(Json.parse("""{"phoneNumber" : "07843274323"}"""))
          .futureValue

      response.status shouldBe 200
    }

    "respond with 400 status when data is invalid" in {
      val response =
        wsClient
          .url(s"$baseUrl/customer-insight-platform/phone-number/verify")
          .post(Json.parse("""{"phoneNumber" : "aaaa"}"""))
          .futureValue

      response.status shouldBe 400
    }
  }
}
