package terraform

import io.circe.parser.decode
import io.circe.generic.auto.*
import terraform.parser.{TerraformProviderConfig, generateCaseClasses}

import scala.io.Source

object A {
  case class B() {

  }
}

@main
def main(): Unit = {
  val b = A.B()

  // read from file
  val source = Source.fromFile("./terraform-to-json/results/yandex.json")
  val jsonStr = source.getLines().mkString
  source.close()

  val terraformProviderConfig = decode[TerraformProviderConfig](jsonStr)

  terraformProviderConfig match {
    case Right(config) =>
      val caseClassDef = generateCaseClasses(config)
      println(s"Generated case class definition:\n$caseClassDef")
    case Left(error) => println(s"Error parsing JSON: $error")
  }
}