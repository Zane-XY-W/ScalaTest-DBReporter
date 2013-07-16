import sbt._
import sbt.Keys._

object DbreporterBuild extends Build {

  lazy val dbreporter = Project(
    id = "dbreporter",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "DBReporter",
      organization := "x.y.z",
      version := "0.1",
      scalaVersion := "2.10.2"
      // add other settings here
    )
  )
}
