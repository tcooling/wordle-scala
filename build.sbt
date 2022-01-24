ThisBuild / organization := "com.tcooling"
ThisBuild / name := "wordle-scala"
ThisBuild / scalaVersion := "2.12.15"
ThisBuild / scalafixDependencies += "com.nequissimus" %% "sort-imports" % "0.6.1"
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
      "org.typelevel" %% "cats-core" % "2.3.0",
      "org.scalamock" %% "scalamock" % "4.4.0" % Test,
      "org.scalatest" %% "scalatest" % "3.0.4" % Test
    )
  )

lazy val defaultSettings = Seq(
  Test / parallelExecution := false,
  Test / fork := true,
  scalafmtOnCompile := true,
  Compile / scalacOptions ++= Seq(
    "-encoding",
    "UTF-8",
    "-deprecation",
    "-unchecked",
    "-Ywarn-dead-code",
    "-Ywarn-unused-import",
    "-Xfatal-warnings",
    "-target:jvm-1.8",
    "-feature",
    "-language:postfixOps",
    "-language:implicitConversions",
    "-language:reflectiveCalls",
    "-language:higherKinds",
    "-language:existentials"
  )
)
