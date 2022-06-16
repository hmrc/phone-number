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

import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.Status.OK
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import play.api.mvc.Results.Ok
import play.api.test.Helpers.{POST, contentAsJson, defaultAwaitTimeout, status}
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.cipphonenumber.connectors.VerifyConnector
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.test.HttpClientV2Support

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class VerifyControllerSpec extends AnyWordSpec
  with Matchers
  with MockitoSugar
  with HttpClientV2Support {

  val expectedValidPasscodeResponseJson = Json.obj("message" -> Json.toJson("Verified"))
  val response: Future[Result] = Future.successful(Ok(expectedValidPasscodeResponseJson))

  val mockVerifyConnector: VerifyConnector = mock[VerifyConnector]
  when(mockVerifyConnector.callService(any[JsValue])(any[ExecutionContext], any[HeaderCarrier])) thenReturn response

  val requestJsonCaptor = ArgumentCaptor.forClass(classOf[JsValue])

  val controller = new VerifyController(Helpers.stubControllerComponents(), mockVerifyConnector)

  private val fakeRequest = FakeRequest(POST, "/validate-details")
  val requestJson: JsValue = Json.obj("phoneNumber" -> Json.toJson("01292123456"), "passcode" -> Json.toJson("ABCDEF"))

  "POST /" should {
    "return 200 with verified" in {
      val actual = controller.verifyPhoneNumber()(
        fakeRequest.withBody(requestJson)
      )

      status(actual) shouldBe OK
      contentAsJson(actual) shouldBe expectedValidPasscodeResponseJson
      verify(mockVerifyConnector).callService(requestJsonCaptor.capture())(any(), any())

      requestJsonCaptor.getValue shouldBe requestJson
    }

  }

}
