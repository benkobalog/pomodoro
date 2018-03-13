name := """pomodoro"""
organization := "pomodoro"

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, FlywayPlugin)

scalaVersion := "2.12.3"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies += "org.postgresql" % "postgresql" % "42.2.1"
libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick-codegen" % "3.2.2",
  "com.typesafe.slick" %% "slick" % "3.2.2",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.2.2",
  "org.slf4j" % "slf4j-nop" % "1.6.4"
)

flywayUrl := "jdbc:postgresql://localhost:5432/pomodoro"
flywayUser := "postgres"
// TODO Nasty workaround for not being able to set env vars for intellij sbt..
flywayPassword := sys.env.getOrElse("POMODORO_PASS", "")
flywayLocations += "db/migration"


lazy val schemaGenArgs = {
  val slickDriver = "slick.jdbc.PostgresProfile"
  val jdbcDriver = "org.postgresql.Driver"
  val url = "jdbc:postgresql://localhost:5432/pomodoro"
  val outputFolder = "app"
  val `package` = "repository.dao"
  val user = "postgres"
  val password = sys.env.getOrElse("POMODORO_PASS", "")
  Array(slickDriver, jdbcDriver, url, outputFolder, `package`, user, password)
}

lazy val genPsqlSchema = taskKey[Unit]("Generate Schema from a PSQL DB")
fullRunTask(genPsqlSchema,
            Compile,
            "slick.codegen.SourceCodeGenerator",
            schemaGenArgs: _*)
