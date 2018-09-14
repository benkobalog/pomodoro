addSbtPlugin("io.github.davidmweber" % "flyway-sbt" % "5.0.0-RC2")
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.12")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.25")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "0.6.0")

addCompilerPlugin(
  "org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
