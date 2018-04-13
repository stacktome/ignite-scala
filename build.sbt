name := "ignite-scala"

organization := "com.jasonmar"

version := "1.7.1"

scalaVersion := "2.12.4"

libraryDependencies += "org.apache.ignite" % "ignite-core" % "2.4.0"

libraryDependencies += "org.apache.ignite" % "ignite-urideploy" % "2.4.0"

libraryDependencies += "org.apache.ignite" % "ignite-indexing" % "2.4.0"

libraryDependencies += "com.github.scopt" %% "scopt" % "3.7.0"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"
