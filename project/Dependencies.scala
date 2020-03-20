import sbt.Keys._
import sbt._

object Dependencies {

  object Version {
    val cats = "2.1.1"
    val catsMtl = "0.7.0"
    val kittens = "2.0.0"
    val janino = "3.1.1"
    val logbackClassic = "1.2.3"
    val newtype = "0.4.3"
    val refined = "0.9.13"
    val scalaCheckShapeless = "1.2.5"
    val scalaLogging = "3.9.2"
    val scalaTest = "3.1.1"
    val scalaTestPlus = "3.1.1.1"
    val scalafixSortImports = "0.3.2"
  }

  lazy val core: Seq[Setting[_]] = deps(
    "eu.timepit" %% "refined" % Version.refined,
    "io.estatico" %% "newtype" % Version.newtype,
    "org.typelevel" %% "cats-free" % Version.cats,
    "org.typelevel" %% "cats-mtl-core" % Version.catsMtl,
    "org.typelevel" %% "kittens" % Version.kittens
  )

  lazy val logging: Seq[Setting[_]] = deps(
    "ch.qos.logback" % "logback-classic" % Version.logbackClassic,
    "com.typesafe.scala-logging" %% "scala-logging" % Version.scalaLogging,
    "org.codehaus.janino" % "janino" % Version.janino
  )

  lazy val test: Seq[Setting[_]] = deps(
    "org.scalatest" %% "scalatest" % Version.scalaTest % Test,
    "org.scalatestplus" %% "scalacheck-1-14" % Version.scalaTestPlus,
    "com.github.alexarchambault" %% "scalacheck-shapeless_1.14" % Version.scalaCheckShapeless % Test
  )

  private def deps(modules: ModuleID*): Seq[Setting[_]] = Seq(libraryDependencies ++= modules)
}
