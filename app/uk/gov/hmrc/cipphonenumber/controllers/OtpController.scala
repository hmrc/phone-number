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

import play.api.libs.json.JsValue
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.cipphonenumber.connectors.VerifyConnector
import uk.gov.hmrc.http.HttpReads.{is2xx, is4xx, is5xx}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton()
class OtpController @Inject()(cc: ControllerComponents, verifyConnector: VerifyConnector)
                             (implicit executionContext: ExecutionContext)
  extends BackendController(cc) {

  def verifyOtp: Action[JsValue] = Action.async(parse.json) { implicit request =>
    verifyConnector.verifyOtp(request.body) map {
      case r if is2xx(r.status) => Ok(r.json)
      case r if is4xx(r.status) => BadRequest(r.json)
      case r if is5xx(r.status) => InternalServerError
    }
  }
}
