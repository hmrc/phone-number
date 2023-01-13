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

package uk.gov.hmrc.cipphonenumber.connectors

import akka.stream.Materializer
import play.api.Logging
import play.api.libs.json.JsValue
import play.api.libs.ws.writeableOf_JsValue
import uk.gov.hmrc.cipphonenumber.config.{AppConfig, CircuitBreakerConfig}
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

@Singleton
class VerifyConnector @Inject()(httpClientV2: HttpClientV2, config: AppConfig)
                               (implicit ec: ExecutionContext, protected val materializer: Materializer) extends Logging with CircuitBreakerWrapper {
  private val verificationServiceHost = s"${config.verificationConfig.protocol}://${config.verificationConfig.host}:${config.verificationConfig.port}"
  private val phoneNumberPath = s"$verificationServiceHost/customer-insight-platform/phone-number"
  private val verifyUrl = s"$phoneNumberPath/verify"
  private val notificationsUrl = s"$phoneNumberPath/notifications/%s"
  private val verifyPasscodeUrl = s"$phoneNumberPath/verify/passcode"

  private val timeout = Duration(config.httpTimeout, "milliseconds")

  implicit val connectionFailure: Try[HttpResponse] => Boolean = {
    case Success(_) => false
    case Failure(_) => true
  }

  def verify(body: JsValue)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    withCircuitBreaker[HttpResponse](
      httpClientV2
        .post(url"$verifyUrl")
        .transform(_.withRequestTimeout(timeout))
        .withBody(body)
        .execute[HttpResponse]
    )
  }

  def status(notificationId: String)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    withCircuitBreaker[HttpResponse](
      httpClientV2
        .get(url"${notificationsUrl.format(notificationId)}")
        .transform(_.withRequestTimeout(timeout))
        .execute[HttpResponse]
    )
  }

  def verifyPasscode(body: JsValue)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    withCircuitBreaker[HttpResponse](
      httpClientV2
        .post(url"$verifyPasscodeUrl")
        .transform(_.withRequestTimeout(timeout))
        .withBody(body)
        .execute[HttpResponse]
    )
  }

  override def configCB: CircuitBreakerConfig = config.verificationConfig.cbConfig
}
