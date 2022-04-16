ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"


lazy val dependencies = Seq(
  "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
  "ch.qos.logback" % "logback-classic" % "1.2.10",
  "org.scalatest" %% "scalatest" % "3.2.11" % "test"
)

lazy val root = (project in file("."))
  .settings(
    name := "traffic-counter",
    libraryDependencies ++= dependencies,
    assembly / mainClass := Some("org.abc.trafficcounter.Main"),
    assembly / assemblyJarName := "trafficcounter.jar"
  )
