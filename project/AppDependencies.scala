import play.core.PlayVersion
import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-backend-play-28"  % "5.21.0"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"     % "5.21.0"             % "test, it",
    "com.vladsch.flexmark"    %  "flexmark-all"               % "0.36.8"            % "test, it",
//    "org.scalatest"          %% "scalatest"                % "3.2.9"         % "test, it",
//    "com.vladsch.flexmark"   % "flexmark-all"              % "0.35.10"       % "test,it",
//    "com.typesafe.play"      %% "play-test"                % current         % "test, it",
    "com.github.tomakehurst" % "wiremock-jre8"             % "2.26.3"        % "test, it",
    "uk.gov.hmrc"            %% "service-integration-test" % "1.1.0-play-28" % "test, it",
    "org.mockito"            %% "mockito-scala-scalatest"  % "1.15.0"        % "test,it"
  )
}
