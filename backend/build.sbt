val slickVersion = "3.2.2"
val circeVersion = "0.9.1"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,

  //  "com.typesafe.slick" %% "slick-codegen" % slickVersion,
  //  "com.h2database" % "h2" % "1.4.192",
  "org.postgresql" % "postgresql" % "42.2.1",
  "com.typesafe.slick" %% "slick" % slickVersion,
  "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,

  "org.mindrot" % "jbcrypt" % "0.3m",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.1.3" % "test",
  "com.typesafe.akka" %% "akka-http" % "10.1.3",
  "com.typesafe.akka" %% "akka-stream" % "2.5.12",

  "ch.qos.logback" % "logback-classic" % "1.2.3",

  "org.scalatest" % "scalatest_2.12" % "3.0.5" % "test"
)
