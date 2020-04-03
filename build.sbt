import sbt._
import sbt.Keys._

name := "ignite-scala"

organization := "com.jasonmar"

version := "1.7.5-SNAPSHOT"

scalaVersion := "2.12.8"

libraryDependencies ++= {
  val igniteV       = "2.8.0"
  Seq(
    "org.apache.ignite" % "ignite-core" % igniteV,
    "org.apache.ignite" % "ignite-urideploy" % igniteV,
    "org.apache.ignite" % "ignite-indexing" % igniteV,
    "org.apache.ignite" % "ignite-kubernetes" % igniteV,
    "com.github.scopt" %% "scopt" % "3.7.0",
    "org.scalatest" %% "scalatest" % "3.0.4" % "test",
    "ch.qos.logback" % "logback-classic" % "1.1.4"
  )
}

updateOptions := updateOptions.value.withGigahorse(false)
publishTo := {
  val nexus = "https://nexus.stacktome.com"

  if (isSnapshot.value)
    Some("snapshots" at nexus + "/repository/maven-snapshots")
  else
    Some("releases"  at nexus + "/repository/maven-releases")
}
credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")