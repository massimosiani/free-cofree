import sbt.Keys._
import sbt._

object Settings {

  private lazy val general = Seq(
    scalaVersion      :=  "2.13.1",
    organization      :=  "free",
    scalacOptions     ++= Seq("-Xlint") ++ Seq("-Ymacro-annotations", "-Yrangepos", "-Ywarn-unused", "-Wunused"),
    scalacOptions -= "-Xfatal-warnings",
    scalacOptions in (Compile, console) --= Seq("-Ywarn-unused:imports", "-Xfatal-warnings")
  )

  private lazy val shared = general ++ Testing.settings

  private lazy val allDeps = Dependencies.core ++ Dependencies.logging ++ Dependencies.test

  lazy val core = shared ++ allDeps
}
