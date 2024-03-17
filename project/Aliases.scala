import sbt.*

object Aliases {

  lazy val defineCommandAliases: Seq[Def.Setting[State => State]] = scalafix ++ scalaFmt ++ prBuild

  private lazy val scalafix =
    addCommandAlias(
      "checkFix",
      "Compile / scalafix --check; Test / scalafix --check;"
    ) ++
      addCommandAlias(
        "runFix",
        "Compile / scalafix; Test / scalafix;"
      )

  private lazy val scalaFmt =
    addCommandAlias(
      name = "checkFmt",
      value = "scalafmtCheckAll; scalafmtSbtCheck"
    ) ++
      addCommandAlias(
        name = "runFmt",
        value = "scalafmtAll; scalafmtSbt"
      )

  private lazy val prBuild = addCommandAlias("prBuild", "; checkFmt; checkFix; test")

}
