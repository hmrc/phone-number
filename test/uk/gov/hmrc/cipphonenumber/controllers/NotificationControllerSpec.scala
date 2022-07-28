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

package uk.gov.hmrc.cipphonenumber.controllers

import org.mockito.ArgumentMatchersSugar.any
import org.mockito.IdiomaticMockito
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR}
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.cipphonenumber.connectors.VerifyConnector
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class NotificationControllerSpec extends AnyWordSpec
  with Matchers
  with IdiomaticMockito {

  private val fakeRequest = FakeRequest()
  private val mockVerifyConnector: VerifyConnector = mock[VerifyConnector]
  private val controller = new NotificationController(Helpers.stubControllerComponents(), mockVerifyConnector)

  "status" should {
    "convert upstream 200 response" in {
      mockVerifyConnector.status("test-notification-id")(any[HeaderCarrier])
        .returns(Future.successful(HttpResponse(OK, """{"m":"m"}""")))

      val response = controller.status("test-notification-id")(fakeRequest)

      status(response) shouldBe OK
      contentAsJson(response) shouldBe Json.parse("""{"m":"m"}""")
    }

    "convert upstream 400 response" in {
      mockVerifyConnector.status("test-notification-id")(any[HeaderCarrier])
        .returns(Future.successful(HttpResponse(BAD_REQUEST, """{"m":"m"}""")))

      val response = controller.status("test-notification-id")(fakeRequest)

      status(response) shouldBe BAD_REQUEST
      contentAsJson(response) shouldBe Json.parse("""{"m":"m"}""")
    }

    "convert upstream 500 response" in {
      mockVerifyConnector.status("test-notification-id")(any[HeaderCarrier])
        .returns(Future.successful(HttpResponse(INTERNAL_SERVER_ERROR, "")))

      val response = controller.status("test-notification-id")(fakeRequest)

      status(response) shouldBe INTERNAL_SERVER_ERROR
    }
  }
}
