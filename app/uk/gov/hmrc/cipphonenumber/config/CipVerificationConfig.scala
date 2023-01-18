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

package uk.gov.hmrc.cipphonenumber.config

import play.api.{ConfigLoader, Configuration}

case class CipVerificationConfig(
                                  protocol: String,
                                  host: String,
                                  port: Int,
                                  authToken: String,
                                  cbConfig: CircuitBreakerConfig
                                ) {
  lazy val url: String = s"$protocol://$host:$port"
}

object CipVerificationConfig {
  implicit lazy val configLoader: ConfigLoader[CipVerificationConfig] =
    ConfigLoader {
      rootConfig =>
        path =>
          val config = Configuration(rootConfig.getConfig(path))
          CipVerificationConfig(
            config.get[String]("protocol"),
            config.get[String]("host"),
            config.get[Int]("port"),
            config.get[String]("auth-token"),
            config.get[CircuitBreakerConfig]("circuit-breaker")
          )
    }
}
