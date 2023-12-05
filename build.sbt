import _root_.io.github.davidgregory084.TpolecatPlugin.autoImport.{ScalacOptions, tpolecatScalacOptions}

ThisBuild / organization := "com.tcooling"
ThisBuild / name := "wordle-scala"
ThisBuild / scalaVersion := "3.3.1"
ThisBuild / version := "0.0.1"
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision
Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val wordleScala = Project(id = "wordle-scala", base = file("."))
  .settings(defaultSettings: _*)
  .settings(Aliases.defineCommandAliases)
  .settings(mainClass := Some("com.tcooling.wordle.Main"))
  .settings(
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "2.10.0",
      "org.scalamock" %% "scalamock" % "4.4.0"  % Test cross CrossVersion.for3Use2_13,
      "org.scalatest" %% "scalatest" % "3.2.17" % Test
    )
  )

lazy val defaultSettings = Seq(
  Test / parallelExecution := false,
  Test / fork := true,
  scalafmtOnCompile := true,
  tpolecatScalacOptions ++= Set(
    ScalacOptions.other("-no-indent"),
    ScalacOptions.other("-old-syntax"),
    ScalacOptions.other("-Wunused:all")
  )
)
