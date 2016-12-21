name := """play-morphia"""

organization := "it.unifi.cerm"

description := "Play 2.5.x Module for Morphia http://mongodb.github.io/morphia/"

version := "1.0"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.mongodb" % "mongo-java-driver" % "3.2.2",
  "org.mongodb.morphia" % "morphia" % "1.3.0"
)
