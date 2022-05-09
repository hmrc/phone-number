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

package uk.gov.hmrc.cipphonenumber.connectors

import play.api.Logging
import play.api.libs.json.JsValue
import play.api.libs.ws.ahc.AhcCurlRequestLogger
import play.api.libs.ws.{WSClient, WSResponse, writeableOf_JsValue}
import play.api.mvc.Results.{BadRequest, Ok}
import play.api.mvc.{Request, Result}
import uk.gov.hmrc.cipphonenumber.config.AppConfig

import javax.inject.Inject
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

class PhoneNumberValidateConnector @Inject()(wsClient: WSClient, config: AppConfig) extends Logging{

  val validateUrl = s"${config.validateUrlProtocol}://${config.validateUrlHost}:${config.validateUrlPort}/customer-insight-platform/phone-number/validate-format"

  val validateHeaders: (String, String) = ("Content-Type", "application/json")

  def callService(implicit ec: ExecutionContext, request: Request[JsValue]): Future[Result] = {

    def parseResponse(response: WSResponse) = response match {
      case response if response.status >= 400 && response.status < 500 => BadRequest(response.body)
      case response if response.status == 200 => Ok
    }

    val response = wsClient.url(validateUrl)
      .addHttpHeaders(validateHeaders)
      .withRequestTimeout(10.seconds)
      .withRequestFilter(AhcCurlRequestLogger())
      .post(request.body)

    response map parseResponse recoverWith {
        case e: Throwable =>
          logger.error(s"Downstream call failed: ${config.validateUrlHost}")
          Future.failed(e)
        }
  }
}
