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

import play.api.libs.json.JsValue
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.cipphonenumber.connectors.VerifyConnector
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton()
class VerifyController @Inject()(cc: ControllerComponents, verifyConnector: VerifyConnector)
                                (implicit executionContext: ExecutionContext)
  extends BackendController(cc) {

  def verify: Action[JsValue] = Action.async(parse.json) { implicit request =>
    verifyConnector.verify(request.body) map {
      response =>
        val headers = response.headers.toSeq flatMap {
          case (parameter, values) =>
            values map (parameter -> _)
        }
        Status(response.status)(response.body).withHeaders(headers: _*)
    }
  }
}
