name := "vertx-akka"

version := "1.0"

scalaVersion := "2.10.3"

scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation", "-feature")

libraryDependencies ++= Seq(
  "com.googlecode.concurrentlinkedhashmap"  %   "concurrentlinkedhashmap-lru" % "1.4",
  "com.typesafe.play" % "play-json_2.10" % "2.2.1",
  "com.github.nscala-time" %% "nscala-time" % "0.8.0",
  //"com.sksamuel.elastic4s" %% "elastic4s" % "1.0.0.0",
  "org.elasticsearch" % "elasticsearch" % "1.0.0",
"com.typesafe" %% "scalalogging-slf4j" % "1.0.1",
"org.slf4j" % "slf4j-api" % "1.7.1",
"org.slf4j" % "log4j-over-slf4j" % "1.7.1",  // for any java classes looking for this
"ch.qos.logback" % "logback-classic" % "1.0.3"
)

resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases"

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"