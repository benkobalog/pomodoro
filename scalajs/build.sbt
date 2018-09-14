scalaJSUseMainModuleInitializer := true

lazy val js = TaskKey[Unit]("js")

addCompilerPlugin(
  "org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

js := {
  val oldTarget = (target in Compile).value.getAbsolutePath
  val base = baseDirectory.value.getAbsolutePath
  val newJsFile = new File(base + "/../frontend/public/javascripts/main.js")

  import java.io.FileInputStream
  import java.io.FileOutputStream
  val inputChannel =
    new FileInputStream(oldTarget + "/scala-2.12/scalajs-fastopt.js").getChannel
  val outputChannel = new FileOutputStream(newJsFile).getChannel
  outputChannel.transferFrom(inputChannel, 0, inputChannel.size)
  inputChannel.close()
  outputChannel.close()
  println("Copied JS output")
}

js := (js dependsOn (Compile / fastOptJS)).value

val circeVersion = "0.9.1"

libraryDependencies ++= Seq(
  "com.softwaremill.sttp" %%% "core" % "1.3.3",

  "com.thoughtworks.binding" %%% "binding" % "11.0.1",
  "com.thoughtworks.binding" %%% "dom" % "11.0.1",

  "io.circe" %%% "circe-core" % circeVersion,
  "io.circe" %%% "circe-generic" % circeVersion,
  "io.circe" %%% "circe-parser" % circeVersion,

//  "org.scala-lang.modules" %% "scala-xml" % "1.1.0",

  "org.scala-js" %%% "scalajs-dom" % "0.9.6"
  )