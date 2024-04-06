package terraform

import io.circe.parser.decode
import io.circe.generic.auto.*
import terraform.parser.{DocsParser, TerraformProviderConfig, generateCaseClasses}

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
    Files.write(Paths.get(filePath), classCode.getBytes)
  }
}


@main
def main(): Unit = {
  var s = s"${/*""*/s""}"
  // Assuming you have the JSON string
  var source = Source.fromFile("./terraform-docs-extractor/results/aws.json")
  val jsonString = source.getLines().mkString
  source.close()

  val parsedDocs = DocsParser.decodeAndFilterJson(jsonString)

//  println(s"Domains:\n\n${parsedDocs.domains.map((key, value) => s"$key -> $value").mkString("\n")}")
//  println(s"IPs:\n\n${parsedDocs.ips.map((key, value) => s"$key -> $value").mkString("\n")}")
//  println(s"IP Masks:\n\n${parsedDocs.ipMasks.map((key, value) => s"$key -> $value").mkString("\n")}")
//  println(s"JSON Strings:\n\n${parsedDocs.jsonStrings.map({case (key, (value, t)) => s"$key -> $value: $t"}).mkString("\n")}")
  println(s"Field Links:\n\n${parsedDocs.fieldLinks.map((key, value) => s"$key -> $value").mkString("\n")}")
  println(s"Field Links count: ${parsedDocs.fieldLinks.size}")

  // read from file
  source = Source.fromFile("./terraform-to-json/results/aws.json")
  val jsonStr = source.getLines().mkString
  source.close()

  val terraformProviderConfig = decode[TerraformProviderConfig](jsonStr)

  terraformProviderConfig match {
    case Right(config) =>
      val generatedPackages = generateCaseClasses(config, "terraform.providers.aws", "AWS", parsedDocs)

      val basePath = "./hcl_generator/src/main/scala"
      generatedPackages.foreach { case (packageName, classes) =>
        createClassFile(packageName, classes, basePath)
      }
    case Left(error) => println(s"Error parsing JSON: $error")
  }
}