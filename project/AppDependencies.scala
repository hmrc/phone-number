import sbt.*

object AppDependencies {
  private val bootstrapPlayVersion = "8.1.0"

  val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-backend-play-30"    % bootstrapPlayVersion,
    "uk.gov.hmrc" %% "internal-auth-client-play-30" % "1.9.0"
  )

  val test = Seq(
    "uk.gov.hmrc"       %% "bootstrap-test-play-30"  % bootstrapPlayVersion % Test,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-test-play-30" % "1.6.0"              % Test
  )
}
