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
import uk.gov.hmrc.cipphonenumber.utils.DataSteps

import scala.util.Random

class VerifyPasscodeIntegrationSpec
  extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with GuiceOneServerPerSuite
    with DataSteps {

  private val phoneNumberRandomizer = Random.alphanumeric.filter(_.isDigit).take(9).mkString

  "verify/passcode" should {
    "respond with 200 verified status with valid phone number and passcode" in {
      val phoneNumber = s"07$phoneNumberRandomizer"
      val normalisedPhoneNumber = s"+447$phoneNumberRandomizer"
      //generate passcode
      verify(phoneNumber).futureValue

      //retrieve passcode
      val maybePhoneNumberAndPasscode = retrievePasscode(normalisedPhoneNumber).futureValue

      //verify passcode (sut)
      val response =
        wsClient
          .url(s"$baseUrl/customer-insight-platform/phone-number/verify/passcode")
          .withHttpHeaders(("Authorization", "fake-token"))
          .withRequestFilter(AhcCurlRequestLogger())
          .post(Json.parse {
            s"""{
               "phoneNumber": "$normalisedPhoneNumber",
               "passcode": "${maybePhoneNumberAndPasscode.get.passcode}"
               }""".stripMargin
          })
          .futureValue

      response.status shouldBe 200
      (response.json \ "status").as[String] shouldBe "Verified"
    }

    "respond with 200 not verified status with non existent passcode" in {
      //verify passcode (sut)
      val response =
        wsClient
          .url(s"$baseUrl/customer-insight-platform/phone-number/verify/passcode")
          .withHttpHeaders(("Authorization", "fake-token"))
          .withRequestFilter(AhcCurlRequestLogger())
          .post(Json.parse {
            s"""{
               "phoneNumber": "07777777777",
               "passcode": "123456"
               }""".stripMargin
          })
          .futureValue

      response.status shouldBe 200
      (response.json \ "code").as[Int] shouldBe 1003
      (response.json \ "message").as[String] shouldBe "Enter a correct passcode"
    }

    // invalid request
    "respond with 400 status for invalid request" in {
      val response =
        wsClient
          .url(s"$baseUrl/customer-insight-platform/phone-number/verify/passcode")
          .withHttpHeaders(("Authorization", "fake-token"))
          .withRequestFilter(AhcCurlRequestLogger())
          .post(Json.parse {
            s"""{
               "phoneNumber": "07777777777",
               "passcode": ""
               }""".stripMargin
          })
          .futureValue

      response.status shouldBe 400
      (response.json \ "code").as[Int] shouldBe 1002
      (response.json \ "message").as[String] shouldBe "Enter a valid passcode"
    }
  }
}
