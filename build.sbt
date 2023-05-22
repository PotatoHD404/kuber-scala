ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.2.2"

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
      "io.circe" %% "circe-parser" % "0.14.5"
    )
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
    )
  )

lazy val root = (project in file("."))
  .aggregate(hclGenerator, autoscaler)
  .settings(
    name := "kuber-scala"
  )
