import play.core.PlayVersion
import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val hmrcBootstrapVersion = "5.23.2-RC2"

  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-backend-play-28"  % hmrcBootstrapVersion
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"     % hmrcBootstrapVersion            % "test, it",
    "org.scalatestplus"       %% "mockito-3-12"                % "3.2.10.0"                      % "test, it",
    "com.vladsch.flexmark"    %  "flexmark-all"               % "0.62.2"                        % "test, it"
  )
}

