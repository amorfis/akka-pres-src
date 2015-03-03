name := "akka-pres"

version := "1.0"

scalaVersion := "2.11.5"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.9",
  "com.typesafe.akka" %% "akka-remote" % "2.3.9",
  "org.twitter4j" % "twitter4j-core" % "4.0.2",
  "org.twitter4j" % "twitter4j-stream" % "4.0.2",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.5.1",
  "joda-time" % "joda-time" % "2.7",
//  "org.boofcv" % "xuggler" % "0.17"
  "xuggle" % "xuggle-xuggler" % "5.4",
  "io.humble" % "humble-video-all" % "0.2.1"
)

resolvers += "xuggler-repo" at "http://xuggle.googlecode.com/svn/trunk/repo/share/java"
