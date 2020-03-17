import sbt.Keys._
import sbt._

object Testing {

  private lazy val testSettings = Seq(
    fork in Test := false,
    parallelExecution in Test := false
  )

  lazy val settings = testSettings
}
