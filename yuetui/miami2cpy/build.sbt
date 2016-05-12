name := """miami"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala).enablePlugins(SbtWeb)

scalaVersion := "2.11.7"

sources in (Compile, doc) := Seq.empty

publishArtifact in (Compile, packageDoc) := false

publishArtifact in packageDoc := false


libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"


libraryDependencies ++= {

  val akkaV = "2.4.2"
  val playSlickV = "1.1.1"
  val nscalaTimeV = "2.6.0"
  val mysqlConnectorV = "5.1.31"
  val slickV = "3.1.0"
  val codecV = "1.9"


  Seq(
    "com.typesafe.play" %% "play-slick" % playSlickV,
    "com.typesafe.slick" %% "slick" % slickV withSources(),
    "com.typesafe.slick" %% "slick-codegen" % slickV,
    "com.typesafe.akka" %% "akka-actor" % akkaV withSources(),
    "com.typesafe.akka" %% "akka-remote" % akkaV withSources(),
    "com.typesafe.akka" %% "akka-slf4j" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "mysql" % "mysql-connector-java" % mysqlConnectorV,
    "com.github.nscala-time" %% "nscala-time" % nscalaTimeV,
    "commons-codec" % "commons-codec" % codecV
  )

}



pipelineStages := Seq(digest, gzip)


