import sbt._
import sbt.Keys.{organization, _}

val copyJarToBin: TaskKey[Unit] = taskKey("run assembly copy jar to ../bin dir")

lazy val customSqlFunc = (project in file("custom-sql-func/"))
  .settings(
    name := "custom-sql-func",
    organization := "com.stacktome",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.8",
    crossScalaVersions := Seq("2.11.8", "2.12.8"),
    //    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    //    buildInfoPackage := "stacktome",
    updateOptions := updateOptions.value.withGigahorse(false),
    publishTo := {
      val nexus = "https://nexus.stacktome.com"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "/repository/maven-snapshots")
      else
        Some("releases" at nexus + "/repository/maven-releases")
    },
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
    libraryDependencies += "org.apache.ignite" % "ignite-core" % "2.8.0",
    libraryDependencies += "org.scalatest"     %% "scalatest"  % "3.0.5" % Test,
    assemblyJarName in assembly := s"custom-sql-func.jar",
    copyJarToBin := {
      import sys.process._
      val cmd = s"cp ${assembly.value} ../bin"
      println(cmd)
      cmd.!
    }
  )

lazy val igniteScala = (project in file("."))
  .settings(
    name := "ignite-scala",
    organization := "com.jasonmar",
    version := "1.7.6-SNAPSHOT",
    scalaVersion := "2.12.8",
    crossScalaVersions := Seq("2.11.8", "2.12.8"),
    libraryDependencies ++= {
      val igniteV = "2.8.0"
      Seq(
        "org.apache.ignite" % "ignite-core"       % igniteV,
        "org.apache.ignite" % "ignite-urideploy"  % igniteV,
        "org.apache.ignite" % "ignite-indexing"   % igniteV,
        "org.apache.ignite" % "ignite-kubernetes" % igniteV,
        "com.github.scopt"  %% "scopt"            % "3.7.0",
        "org.scalatest"     %% "scalatest"        % "3.0.4" % "test",
        "ch.qos.logback"    % "logback-classic"   % "1.1.4"
      )
    },
    updateOptions := updateOptions.value.withGigahorse(false),
    publishTo := {
      val nexus = "https://nexus.stacktome.com"

      if (isSnapshot.value)
        Some("snapshots" at nexus + "/repository/maven-snapshots")
      else
        Some("releases" at nexus + "/repository/maven-releases")
    },
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")
  )
  .dependsOn(customSqlFunc)
  .aggregate(customSqlFunc)
