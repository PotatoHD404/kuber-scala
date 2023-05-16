package terraform

import io.circe.parser.decode
import io.circe.generic.auto.*
import terraform.parser.{TerraformProviderConfig, generateCaseClasses}

import java.io.File
import java.nio.file.{Files, Paths}
import scala.io.Source

def createClassFile(packageName: String, classes: List[(String, String)], basePath: String): Unit = {
  val packagePath = packageName.replace(".", "/")
  val fullPath = s"$basePath/$packagePath"

  val directory = new File(fullPath)
  if (!directory.exists()) {
    directory.mkdirs()
  }

  classes.foreach { case (className, classCode) =>
    val filePath = s"$fullPath/$className.scala"
    val fullClassCode = s"package $packageName\n\nimport terraform.HCLImplicits._\n\n$classCode"
    Files.write(Paths.get(filePath), fullClassCode.replace("type:", "`type`:")
      .replace(".type", ".`type`")
      .replace("package:", "`package`:")
      .replace(".package", ".`package`")
      .replace("class:", "`class`:")
      .replace(".class", ".`class`").getBytes)
  }
}


@main
def main(): Unit = {

  // read from file
  val source = Source.fromFile("./terraform-to-json/results/yandex.json")
  val jsonStr = source.getLines().mkString
  source.close()

  val terraformProviderConfig = decode[TerraformProviderConfig](jsonStr)

  terraformProviderConfig match {
    case Right(config) =>
      val generatedPackages = generateCaseClasses(config, "terraform.providers.yandex")

      val basePath = "./src/main/scala"
      generatedPackages.foreach { case (packageName, classes) =>
        createClassFile(packageName, classes, basePath)
      }
    case Left(error) => println(s"Error parsing JSON: $error")
  }
}