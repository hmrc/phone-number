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
import play.api.libs.ws.ahc.AhcCurlRequestLogger
import uk.gov.hmrc.cipphonenumber.utils.DataSteps

import scala.util.Random

class NotificationIntegrationSpec
  extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with GuiceOneServerPerSuite
    with DataSteps {

  "/notifications" should {
    "respond with 200 status with valid notification id" in {
      val phoneNumberRandomizer = Random.alphanumeric.filter(_.isDigit).take(9).mkString

      //generate passcode
      val verifyResponse = verify(s"07$phoneNumberRandomizer").futureValue

      val notificationPath = verifyResponse.header("Location").get

      val response =
        wsClient
          .url(s"$baseUrl/customer-insight-platform/phone-number$notificationPath")
          .withHttpHeaders(("Authorization", "local-test-token"))
          .withRequestFilter(AhcCurlRequestLogger())
          .get
          .futureValue

      response.status shouldBe 200
      (response.json \ "notificationStatus").as[String] shouldBe "DELIVERED"
      (response.json \ "message").as[String] shouldBe "Message was delivered successfully"
    }

    "respond with 404 status when notification id not found" in {
      val response =
        wsClient
          .url(s"$baseUrl/customer-insight-platform/phone-number/notifications/noresult-d385-4b17-a0b4-23a85c0c5b1a")
          .withHttpHeaders(("Authorization", "local-test-token"))
          .withRequestFilter(AhcCurlRequestLogger())
          .get
          .futureValue

      response.status shouldBe 404
      (response.json \ "code").as[Int] shouldBe 1001
      (response.json \ "message").as[String] shouldBe "Notification Id not found"
    }
  }
}
