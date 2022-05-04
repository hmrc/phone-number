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

package uk.gov.hmrc.cipphonenumber.service

import play.api.libs.json.{JsResult, JsSuccess, JsValue, Json}
import play.api.libs.ws.ahc.AhcCurlRequestLogger
import play.api.libs.ws.{WSClient, writeableOf_JsValue}
import play.api.mvc.Request

import javax.inject.Inject
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

class PhoneNumberValidateService @Inject() (wsClient: WSClient){

  //TODO config for url
  val validateUrl = "http://localhost:8082/customer-insight-platform/phone-number/validate-details"

  val validateHeaders: (String, String) = ("Content-Type", "application/json")

  //TODO Error handling ticket
  def callService(implicit ec: ExecutionContext, request: Request[JsValue]): Future[JsResult[String]] = {
    val response: Future[JsResult[String]] = wsClient.url(validateUrl)
      .addHttpHeaders(validateHeaders)
      .withRequestTimeout(10.seconds)
      .withRequestFilter(AhcCurlRequestLogger())
      .post(request.body) map { response =>
        println("-------------")
        println(request.body)
        println("-------------")
        JsSuccess("")
      }

    response.recoverWith {
      case e: Throwable => Future.failed(e)
    }
  }
}

abstract case class PhoneNumber(phoneNumber: String)
