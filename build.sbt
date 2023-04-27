ThisBuild / version := "0.1.0-SNAPSHOT"

libraryDependencies += "io.github.hagay3" %% "skuber" % "3.0.6"

libraryDependencies += "io.spray" %%  "spray-json" % "1.3.6"

ThisBuild / scalaVersion := "3.2.2"

lazy val root = (project in file("."))
  .settings(
    name := "kuber-scala"
  )
