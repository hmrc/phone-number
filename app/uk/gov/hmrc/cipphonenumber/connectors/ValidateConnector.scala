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
import play.api.libs.ws.writeableOf_JsValue
import play.api.mvc.Result
import play.api.mvc.Results.{BadRequest, Ok}
import uk.gov.hmrc.cipphonenumber.config.AppConfig
import uk.gov.hmrc.http.HttpErrorFunctions.{is2xx, is4xx}
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ValidateConnector @Inject()(httpClientV2: HttpClientV2, config: AppConfig)(implicit ec: ExecutionContext) extends Logging {

  def callService(phoneJsValue: JsValue)(implicit hc: HeaderCarrier): Future[Result] = {
    val validateUrl = s"${config.validateUrlProtocol}://${config.validateUrlHost}:${config.validateUrlPort}"

    httpClientV2
      .post(url"$validateUrl/customer-insight-platform/phone-number/validate-format")
      .withBody(phoneJsValue)
      .execute[HttpResponse]
      .flatMap {
        case r if is2xx(r.status) => Future.successful(Ok)
        case r if is4xx(r.status) => Future.successful(BadRequest(r.json))
      } recoverWith {
      case e: Throwable =>
        logger.error(s"Downstream call failed: ${config.validateUrlHost}")
        Future.failed(e)
    }
  }
}
