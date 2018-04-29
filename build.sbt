name := "ignite-scala"

organization := "com.jasonmar"

version := "1.8.0-SNAPSHOT"

scalaVersion := "2.11.11"

libraryDependencies += "org.apache.ignite" % "ignite-core" % "2.4.0"

libraryDependencies += "org.apache.ignite" % "ignite-urideploy" % "2.4.0"

libraryDependencies += "com.github.scopt" %% "scopt" % "3.7.0"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"
