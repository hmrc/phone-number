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
import play.api.libs.ws.ahc.AhcCurlRequestLogger
import uk.gov.hmrc.cipphonenumber.utils.DataSteps

class OtpIntegrationSpec
  extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with GuiceOneServerPerSuite
    with DataSteps {

  "otp" should {
    "respond with 200 verified status with valid otp" in {
      val phoneNumber = "07811123456"
      //generate otp
      verify(phoneNumber).futureValue

      //retrieve otp
      val otp = retrieveOtp(phoneNumber).futureValue

      //verify otp (sut)
      val response =
        wsClient
          .url(s"$baseUrl/customer-insight-platform/phone-number/verify/otp")
          .withRequestFilter(AhcCurlRequestLogger())
          .post(Json.parse {
            s"""{
               "phoneNumber": "$phoneNumber",
               "passcode": "${otp.get.passcode}"
               }""".stripMargin
          })
          .futureValue

      response.status shouldBe 200
      (response.json \ "status").as[String] shouldBe "Verified"
    }

    "respond with 200 not verified status with non existent otp" in {
      val phoneNumber = "07811654321"

      //verify otp (sut)
      val response =
        wsClient
          .url(s"$baseUrl/customer-insight-platform/phone-number/verify/otp")
          .withRequestFilter(AhcCurlRequestLogger())
          .post(Json.parse {
            s"""{
               "phoneNumber": "$phoneNumber",
               "passcode": "123456"
               }""".stripMargin
          })
          .futureValue

      response.status shouldBe 200
      (response.json \ "status").as[String] shouldBe "Not verified"
    }

    "respond with 400 status for invalid request" in {
      val response =
        wsClient
          .url(s"$baseUrl/customer-insight-platform/phone-number/verify/otp")
          .withRequestFilter(AhcCurlRequestLogger())
          .post(Json.parse {
            s"""{
               "phoneNumber": "",
               "passcode": ""
               }""".stripMargin
          })
          .futureValue

      response.status shouldBe 400
      (response.json \ "code").as[String] shouldBe "VALIDATION_ERROR"
      (response.json \ "message").as[String] shouldBe "Validation error"
    }
  }
}
