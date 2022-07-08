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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.Status.OK
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Results.Ok
import play.api.test.Helpers.{defaultAwaitTimeout, status}
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.cipphonenumber.connectors.VerifyConnector
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.test.HttpClientV2Support

import scala.concurrent.Future

class OtpControllerSpec extends AnyWordSpec
  with Matchers
  with MockitoSugar
  with HttpClientV2Support {

  private val fakeRequest = FakeRequest()
  private val mockVerifyConnector: VerifyConnector = mock[VerifyConnector]
  private val controller = new OtpController(Helpers.stubControllerComponents(), mockVerifyConnector)

  "verifyOtp" should {
    "return 200 for valid request" in {
      when(mockVerifyConnector.verifyOtp(any[JsValue])(any[HeaderCarrier])).thenReturn(Future.successful(Ok))

      val actual = controller.verifyOtp(
        fakeRequest.withBody(Json.parse("""{"phoneNumber":"01292123456"}"""))
      )

      status(actual) shouldBe OK
    }
  }
}
