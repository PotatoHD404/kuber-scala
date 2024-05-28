import sbtassembly.AssemblyKeys.{assembly, assemblyMergeStrategy}

ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.2.2"

scalacOptions ++= Seq("-Xmax-classfile-name", "128")

lazy val commonSettings = Seq(
  libraryDependencies ++= Seq(
    "io.circe" %% "circe-core" % "0.14.5",
    "io.circe" %% "circe-generic" % "0.14.5",
    "io.circe" %% "circe-parser" % "0.14.5"
  )
)

lazy val hclGenerator = (project in file("hcl_generator"))
  .settings(
    commonSettings,
    name := "hcl_generator",
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % "0.14.5",
      "io.circe" %% "circe-generic" % "0.14.5",
      "io.circe" %% "circe-parser" % "0.14.5",
      "com.github.javafaker" % "javafaker" % "1.0.2" % Test,
      "org.scalatest" %% "scalatest" % "3.2.18" % Test
    ),
    assembly / mainClass := Some("main"),
    assembly / assemblyJarName := "cluster-creator.jar",
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case _ => MergeStrategy.first
    }
  )

lazy val autoscaler = (project in file("autoscaler"))
  .dependsOn(hclGenerator)
  .settings(
    commonSettings,
    name := "autoscaler",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "2.9.0",
      "org.typelevel" %% "cats-effect" % "3.5.0",
      "co.fs2" %% "fs2-core" % "3.6.1",
      "io.github.hagay3" %% "skuber" % "3.0.6"
    ),
    // Assembly settings
    assembly / mainClass := Some("patched.skuber.main"), // Replace with your main class
    assembly / assemblyJarName := "autoscaler.jar",
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case _ => MergeStrategy.first
    }
  )

lazy val root = (project in file("."))
  .aggregate(hclGenerator, autoscaler)
  .settings(
    name := "kuber-scala"
  )
