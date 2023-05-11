package terraform

import io.circe.parser.decode
import io.circe.generic.auto.*
import terraform.parser.{TerraformProviderConfig, generateCaseClasses}

import java.io.File
import java.nio.file.{Files, Paths}
import scala.io.Source

def createClassFile(packageName: String, className: String, classCode: String, basePath: String): Unit = {
  val packagePath = packageName.replace(".", "/")
  val fullPath = s"$basePath/$packagePath"
  val filePath = s"$fullPath/$className.scala"

  val directory = new File(fullPath)
  if (!directory.exists()) {
    directory.mkdirs()
  }

  Files.write(Paths.get(filePath), classCode.replace("type:", "`type`:")
    .replace("package:", "`package`:")
    .replace(".package", ".`package`")
    .replace("class:", "`class`:").getBytes)
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
      val generatedClasses = generateCaseClasses(config, "terraform.providers.yandex")

      val basePath = "./src/main/scala"
      generatedClasses.foreach { case (packageName, (className, classCode)) =>
        createClassFile(packageName, className, classCode, basePath)
      }
    case Left(error) => println(s"Error parsing JSON: $error")
  }
}