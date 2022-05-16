///*
// * Copyright 2022 HM Revenue & Customs
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package uk.gov.hmrc.cipphonenumber.connectors
//
//import org.scalatest.concurrent.ScalaFutures
//import org.scalatest.matchers.should.Matchers
//import org.scalatest.wordspec.AnyWordSpec
//import org.scalatestplus.play.guice.GuiceOneAppPerSuite
//import play.api.libs.json.{Json, OWrites}
//import play.api.libs.ws.WSRequest
//import play.api.test.FakeRequest
//import uk.gov.hmrc.cipphonenumber.WireMockSupport
//import uk.gov.hmrc.cipphonenumber.config.AppConfig
//import uk.gov.hmrc.cipphonenumber.models.PhoneNumber
//import uk.gov.hmrc.cipphonenumber.utility.FileLoader
//
//class ValidateConnectorSpec
//  extends AnyWordSpec
//    with Matchers
//    with WireMockSupport
//    with GuiceOneAppPerSuite
//    with FileLoader
//    with ScalaFutures {
//
//  val fakeRequest = FakeRequest()
//  implicit val writes: OWrites[PhoneNumber] = Json.writes[PhoneNumber]
//
//  "Connector" should {
//    "get app details" in {
//      wireMockServer.stubFor(
//        post(s"/customer-insight-platform/phone-number/validate-format")
//          .withRequestBody(equalToJson(loadFile("correct_phonenumber.json")))
//          .willReturn(ok()))
//
//      println(wireMockServer.baseUrl())
//
//      val incomingRequest = fakeRequest
//        .withBody(Json.toJson(PhoneNumber("07843274323")))
//      val ec = scala.concurrent.ExecutionContext.Implicits.global
//      val result = connector.callService(ec, incomingRequest).futureValue
//
//      println(result)
//  }
//
//    lazy val connector = {
//      val mockAppConfig = mock[AppConfig]
//      val mockWsRequest = mock[WSRequest]
//      mockWsRequest.withHttpHeaders(any) returns mockWsRequest
//      mockWsRequest.withRequestTimeout(any) returns mockWsRequest
//      mockWsRequest.withRequestFilter(any) returns mockWsRequest
//
//      val mockWsClient = mock[WSClient]
//      mockWsClient.url(any) returns mockWsRequest
//
//      when(mockAppConfig.validateUrlHost).thenReturn("localhost")
//      when(mockAppConfig.validateUrlProtocol).thenReturn("http")
//      when(mockAppConfig.validateUrlPort).thenReturn(s"$wireMockPort")
//      new ValidateConnector(mockWsClient, mockAppConfig)
//    }
//  }
//
//
//}
