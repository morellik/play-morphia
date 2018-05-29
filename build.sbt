name := """play-morphia"""

organization := "it.unifi.cerm"

description := "Play 2.6.x Module for Morphia http://mongodb.github.io/morphia/"

version := "1.2"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  "org.mongodb" % "mongo-java-driver" % "3.7.0",
  "org.mongodb.morphia" % "morphia" % "1.3.2",
  "org.easytesting" % "fest-assert" % "1.4" % "test"
)
