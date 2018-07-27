name := """pomodoro"""
organization := "pomodoro"

lazy val commonSettings = Seq(
  organization := "pomodoro",
  version := "0.1.0",
  scalaVersion := "2.12.6"
)

lazy val root = (project in file("."))
  .enablePlugins(FlywayPlugin)

lazy val frontend = (project in file("frontend"))
  .settings(commonSettings)
  .enablePlugins(PlayScala)

lazy val backend = (project in file("backend"))
  .settings(commonSettings)

flywayUrl := "jdbc:postgresql://localhost:5432/pomodoro"
flywayUser := "postgres"
// TODO Nasty workaround for not being able to set env vars for intellij sbt..
flywayPassword := sys.env.getOrElse("POMODORO_PASS", "")
flywayLocations += "db/migration"
