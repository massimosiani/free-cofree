import Dependencies.Version
import sbt._

// addCompilerPlugin(scalafixSemanticdb)
addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full)
addCommandAlias("fix", "all compile:scalafix test:scalafix; scalafmtAll")
addCommandAlias("fmt", "scalafmtAll")

// scalafixDependencies in ThisBuild += "com.nequissimus" %% "sort-imports" % Version.scalafixSortImports

lazy val free = (project in file("."))
  .settings(Settings.core: _*)
