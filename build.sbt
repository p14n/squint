sbtPlugin := true

name := "squint"

version := "1.0"

scalaVersion := "2.11.2"

scalacOptions += "-deprecation"

libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.1.3"
