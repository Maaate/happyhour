import play.sbt.routes.RoutesKeys

name := """happyhour"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

resolvers ++= Seq[Resolver](Resolver.mavenLocal)

//credentials += Credentials("Sonatype Nexus Repository Manager", "nexus.simplemachines.com.au", "simplemachines", "uX3I1oL7Jaf3Yop5Ad9F")

libraryDependencies ++= Seq(
  jdbc,
  //anorm,
  cache,
  ws,
  "org.slf4j" % "slf4j-nop" % "1.7.25",
  "com.adrianhurt" %% "play-bootstrap" % "1.1-P25-B3",
  "com.typesafe.play" %% "anorm" % "2.5.3",
  "org.postgresql" % "postgresql" % "42.1.3",
  "joda-time" % "joda-time" % "2.9.9",
  "org.joda" % "joda-convert" % "1.8.2",
  "org.jsoup" % "jsoup" % "1.10.3",
  "io.swagger" %% "swagger-play2" % "1.5.3"
)

RoutesKeys.routesImport += "util.Bindables._"