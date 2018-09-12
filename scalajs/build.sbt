scalaJSUseMainModuleInitializer := true

lazy val js = TaskKey[Unit]("js")

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

//Compile / fastOptJS := ((Compile / fastOptJS) dependsOn copyJs).value
js := (js dependsOn (Compile / fastOptJS)).value
