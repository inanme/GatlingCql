version := "0.1"
scalaVersion := "2.12.8"

scalacOptions := Seq(
  "-encoding", "UTF-8", "-target:jvm-1.8", "-deprecation",
  "-feature", "-unchecked", "-language:implicitConversions", "-language:postfixOps")

enablePlugins(GatlingPlugin)

val cassandraDatastaxDriverVersion = "3.6.0"
val gatlingVersion = "2.3.1"
val scalaTestVersion = "3.0.5"
val scalaCheckVersion = "1.14.0"

libraryDependencies ++= Seq(
  "io.gatling.highcharts" % "gatling-charts-highcharts" % gatlingVersion,
  "io.gatling" % "gatling-test-framework" % gatlingVersion,
  "com.datastax.cassandra" % "cassandra-driver-core" % cassandraDatastaxDriverVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
  "org.scalacheck" %% "scalacheck" % scalaCheckVersion % "test",
  "org.easymock" % "easymock" % "3.2" % "test"
)
