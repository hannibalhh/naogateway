name := "naogateway"

version := "1.0"

scalaVersion := "2.10.1"

mainClass in (Compile, run) := Some("naogateway.NaogatewayApp")

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.10" % "2.1.2"

libraryDependencies += "com.typesafe.akka" % "akka-remote_2.10" % "2.1.2"