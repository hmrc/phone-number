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

import akka.pattern.CircuitBreaker
import akka.stream.Materializer
import play.api.Logging
import uk.gov.hmrc.cipphonenumber.config.CircuitBreakerConfig

import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.util.Try

trait CircuitBreakerWrapper extends Logging {
  protected def materializer: Materializer
  def configCB: CircuitBreakerConfig

  def withCircuitBreaker[T](block: => Future[T])(implicit connectionFailure: Try[T] => Boolean): Future[T] =
    circuitBreaker.withCircuitBreaker(block, connectionFailure)

  lazy val circuitBreaker = new CircuitBreaker(
    scheduler = materializer.system.scheduler,
    maxFailures = configCB.maxFailures, // Maximum number of failures before opening the circuit
    callTimeout = configCB.callTimeout, // time after which to consider a call a failure
    resetTimeout = configCB.resetTimeout, // time after which to attempt to close the circuit
    maxResetTimeout = configCB.maxResetTimeout, // max time after which to attempt to close the circuit
    exponentialBackoffFactor = configCB.exponentialBackoffFactor, // exponential time gap e.g. 1 1.2 1.2+1.2*0.2
    randomFactor = configCB.randomFactor //after calculation of the exponential back-off an additional random delay based on this factor is added, e.g. 0.2 adds up to 20% delay.
  )(materializer.executionContext)
    .onOpen(logger.warn(s"Circuit breaker for ${configCB.serviceName} opened and will not close for ${configCB.resetTimeout}"))
    .onHalfOpen(logger.warn(s"Circuit breaker for ${configCB.serviceName} is half open"))
    .onClose(logger.info(s"Circuit breaker for ${configCB.serviceName} has closed"))
    .onCallFailure(_ => logger.warn(s"Circuit breaker for ${configCB.serviceName} recorded failed call"))
    .onCallBreakerOpen(logger.warn(s"Circuit breaker for ${configCB.serviceName} rejected call due to previous failures"))
    .onCallTimeout {
      elapsed =>
        val duration = Duration.fromNanos(elapsed)
        logger.warn(s"Circuit breaker for ${configCB.serviceName} recorded failed call due to timeout after ${duration.toMillis}ms")
    }
}
