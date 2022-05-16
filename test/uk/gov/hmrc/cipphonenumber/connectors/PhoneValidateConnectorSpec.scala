package uk.gov.hmrc.cipphonenumber.connectors

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, equalToJson, post, postRequestedFor, stubFor, urlEqualTo, verify}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Configuration
import play.api.libs.json.JsValue
import play.api.test.FakeRequest
import uk.gov.hmrc.cipphonenumber.WireMockSupport
import uk.gov.hmrc.cipphonenumber.config.AppConfig
import uk.gov.hmrc.cipphonenumber.utility.FileLoader
import uk.gov.hmrc.http.test.HttpClientV2Support
import uk.gov.hmrc.http.{HeaderCarrier, Request, UpstreamErrorResponse}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

class PhoneValidateConnectorSpec extends AnyWordSpec
  with Matchers
  with WireMockSupport
  with GuiceOneAppPerSuite
  with ScalaFutures
  with HttpClientV2Support
  {

  val url: String = "/customer-insight-platform/phone-number/validate-format"

  "ValidatorConnector.callService" should {
    "return HttpResponse for valid input" in new Setup {


      stubFor(
        post(urlEqualTo(url))
          .willReturn(aResponse())
      )

      implicit val request = FakeRequest().withBody("""{
                                                        "phoneNumber": "07890234561"
                                                       }""")
      implicit val hc = HeaderCarrier()
      martinValidateConnector.callService map {
        case s => println(s)
      }

//      verify(
//        postRequestedFor(urlEqualTo(url))
//          .withRequestBody(equalToJson(s"""{
//            "phoneNumber": "07890234561"
//          }"""))
//      )
    }


  }

  trait Setup {
//    implicit val hc = HeaderCarrier()

    private val appConfig = new AppConfig(
      Configuration.from(Map(
        "microservice.services.cipphonenumber.validation.host" -> wireMockHost,
        "microservice.services.cipphonenumber.validation.port" -> wireMockPort
      ))
    )

    val martinValidateConnector = new PhoneValidateConnector(
      httpClientV2,
      appConfig
    )
  }



}
