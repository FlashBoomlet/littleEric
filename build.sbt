// scalastyle:off

ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

mainClass in (Compile, run) := Some("com.flashboomlet.Driver")

lazy val root =
  (project in file(".")).aggregate(
    blueBugatti
  )

lazy val commonSettings = Seq(
  organization := "com.flashboomlet",
  scalaVersion := "2.11.8",
  resolvers ++= Seq(
    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    "Typesafe Releases" at "https://repo.typesafe.com/typesafe/maven-releases/",
    "Maven central" at "http://repo1.maven.org/maven2/"
  ),
  libraryDependencies ++= Seq(
    "org.reactivemongo" %% "reactivemongo" % "0.11.13",
    "com.typesafe.akka" % "akka-actor_2.11" % "2.4.7",
    "ch.qos.logback"  %  "logback-classic" % "1.1.3",
    "org.slf4j" %  "slf4j-api" % "1.7.14",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.7.4",
    "com.github.tototoshi" %% "scala-csv" % "1.3.3"))

lazy val blueBugatti = (project in file ("blueBugatti"))
.settings(commonSettings: _*)
.settings(
  name := "blueBugatti",
  version := "0.0.0"
)
