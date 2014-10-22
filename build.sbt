sbtPlugin := true

name := "blur"

version := "1.0"

scalaVersion := "2.11.2"

libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.1.3"

fork in Test := true
