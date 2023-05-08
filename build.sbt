ThisBuild / version := "0.1.0-SNAPSHOT"

libraryDependencies += "io.github.hagay3" %% "skuber" % "3.0.6"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.9.0",
  "org.typelevel" %% "cats-effect" % "3.4.8"
)



libraryDependencies += "co.fs2" %% "fs2-core" % "3.6.1"



//scalacOptions += "-Xmax-inlines 64" // or a higher value if necessary

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core" % "0.14.5",
  "io.circe" %% "circe-generic" % "0.14.5",
  "io.circe" %% "circe-parser" % "0.14.5"
)

//libraryDependencies ++= Seq(
//  "org.antlr" % "antlr4" % "4.12.0",
//  //  "org.antlr" %% "antlr4-runtime" % "4.12.0"
//)
//
//antlr4Runtime := "com.github.antlr" %% "antlr4-runtime" % "4.9.3-1"
//
//antlr4PackageName := Some("com.mycompany.terraform.parser")
//
//antlr4GenListener := true
//
//antlr4GenVisitor := true
//
//sourceDirectory in antlr4 := (sourceDirectory in Compile).value / "antlr"


ThisBuild / scalaVersion := "3.2.2"

lazy val root = (project in file("."))
  .settings(
    name := "kuber-scala"
  )
