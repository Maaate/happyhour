import play.sbt.routes.RoutesKeys

name := "happyhourv2"
 
version := "1.0" 
      
lazy val `happyhourv2` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
      
scalaVersion := "2.12.2"

libraryDependencies ++= Seq( jdbc,
  ehcache,
  ws,
  specs2 % Test,
  guice,
  "com.adrianhurt" %% "play-bootstrap" % "1.2-P26-B3",
  "org.postgresql" % "postgresql" % "42.1.3",
  "com.typesafe.play" %% "anorm" % "2.5.3",
  "com.typesafe.play" %% "play-json" % "2.6.0",
  "com.typesafe.play" %% "play-json-joda" % "2.6.4",

  // Documentation
  "io.swagger" %% "swagger-play2" % "1.6.0"

)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

RoutesKeys.routesImport += "util.Bindables._"

      