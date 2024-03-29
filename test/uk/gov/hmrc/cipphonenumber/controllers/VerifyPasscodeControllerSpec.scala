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

package uk.gov.hmrc.cipphonenumber.controllers

import org.apache.pekko.stream.ConnectionException
import org.mockito.ArgumentMatchers.{eq => meq, _}
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.Status.{BAD_REQUEST, GATEWAY_TIMEOUT, INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout, status}
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.cipphonenumber.connectors.VerifyConnector
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.internalauth.client.Predicate.Permission
import uk.gov.hmrc.internalauth.client._
import uk.gov.hmrc.internalauth.client.test.StubBehaviour

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class VerifyPasscodeControllerSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "verifyPasscode" should {
    "convert upstream 200 response" in new SetUp {
      when(
        mockVerifyConnector
          .verifyPasscode(meq(Json.parse("""{"req":"req"}""")))(any[HeaderCarrier])
      )
        .thenReturn(Future.successful(HttpResponse(OK, """{"res":"res"}""")))

      private val response = controller.verifyPasscode(
        fakeRequest.withBody(Json.parse("""{"req":"req"}"""))
      )

      status(response) shouldBe OK
      contentAsJson(response) shouldBe Json.parse("""{"res":"res"}""")
      //      mockMetricsService wasNever called
    }

    "convert upstream 400 response" in new SetUp {
      when(
        mockVerifyConnector
          .verifyPasscode(meq(Json.parse("""{"req":"req"}""")))(any[HeaderCarrier])
      )
        .thenReturn(Future.successful(HttpResponse(BAD_REQUEST, """{"res":"res"}""")))

      private val response = controller.verifyPasscode(
        fakeRequest.withBody(Json.parse("""{"req":"req"}"""))
      )

      status(response) shouldBe BAD_REQUEST
      contentAsJson(response) shouldBe Json.parse("""{"res":"res"}""")
      //      mockMetricsService wasNever called
    }

    "convert upstream 500 response" in new SetUp {
      when(
        mockVerifyConnector
          .verifyPasscode(meq(Json.parse("""{"req":"req"}""")))(any[HeaderCarrier])
      )
        .thenReturn(Future.successful(HttpResponse(INTERNAL_SERVER_ERROR, "")))

      private val response = controller.verifyPasscode(
        fakeRequest.withBody(Json.parse("""{"req":"req"}"""))
      )

      status(response) shouldBe INTERNAL_SERVER_ERROR
      //      mockMetricsService wasNever called
    }

    "handle connection exception" in new SetUp {
      when(
        mockVerifyConnector
          .verifyPasscode(meq(Json.parse("""{"req":"req"}""")))(any[HeaderCarrier])
      )
        .thenReturn(Future.failed(new ConnectionException("")))

      private val response = controller.verifyPasscode(
        fakeRequest.withBody(Json.parse("""{"req":"req"}"""))
      )

      status(response) shouldBe GATEWAY_TIMEOUT
      //      mockMetricsService.recordMetric("cip-verify-passcode-failure") was called
    }
  }

  trait SetUp {
    protected val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withHeaders("Authorization" -> "local-test-token")

    private val expectedPredicate =
      Permission(Resource(ResourceType("phone-number"), ResourceLocation("*")), IAAction("*"))
    protected val mockStubBehaviour: StubBehaviour = mock[StubBehaviour]
    when(mockStubBehaviour.stubAuth(Some(expectedPredicate), Retrieval.EmptyRetrieval)).thenReturn(Future.unit)
    protected val mockVerifyConnector: VerifyConnector = mock[VerifyConnector]

    protected lazy val controller =
      new VerifyPasscodeController(Helpers.stubControllerComponents(), mockVerifyConnector)
  }
}
