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
import uk.gov.hmrc.cipphonenumber.config.AppConfig
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VerifyConnector @Inject()(httpClientV2: HttpClientV2, config: AppConfig)
                               (implicit ec: ExecutionContext) extends Logging {
  private val verificationServiceHost = s"${config.verificationUrlProtocol}://${config.verificationUrlHost}:${config.verificationUrlPort}"
  private val phoneNumberPath = s"$verificationServiceHost/customer-insight-platform/phone-number"
  private val verifyUrl = s"$phoneNumberPath/verify"
  private val notificationsUrl = s"$phoneNumberPath/notifications/%s"
  private val verifyOtpUrl = s"$phoneNumberPath/verify/otp"

  def verify(body: JsValue)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    httpClientV2
      .post(url"$verifyUrl")
      .withBody(body)
      .execute[HttpResponse]
  }

  def status(notificationId: String)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    httpClientV2
      .get(url"${notificationsUrl.format(notificationId)}")
      .execute[HttpResponse]
  }

  def verifyOtp(body: JsValue)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    httpClientV2
      .post(url"$verifyOtpUrl")
      .withBody(body)
      .execute[HttpResponse]
  }
}
