import sbt._
import Keys._
import play.Play.autoImport._

object ApplicationBuild extends Build {

  val appName         = "user-service"
  val appVersion      = "1.0.0-SNAPSHOT"
  val commonScalaVersion = "2.11.6"

  val appDependencies = Seq(
    ws,
    cache, // play cache external module
    "com.mohiva" %% "play-silhouette" % "2.0-RC1",
    "com.typesafe.play.plugins" %% "play-plugins-mailer" % "2.3.0",
    "net.codingwell" %% "scala-guice" % "4.0.0-beta5",
    "com.typesafe.play" %% "play-slick" % "0.8.1",
    "com.github.tototoshi" %% "slick-joda-mapper" % "1.2.0",
    "mysql" % "mysql-connector-java" % "5.1.32",
    "org.joda" % "joda-convert" % "1.6",
    "org.scalatestplus" %% "play" % "1.2.0" % "test"
  )

  // main Play project
  lazy val mainProject = Project(appName, file(".")).enablePlugins(play.PlayScala).settings(
    scalaVersion := commonScalaVersion,
    version := appVersion,
    libraryDependencies ++= appDependencies
  )

}
