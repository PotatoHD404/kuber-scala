package terraform

import io.circe.parser.decode
import io.circe.generic.auto.*
import terraform.parser.{TerraformProviderConfig, generateCaseClass}
@main
def main(): Unit = {
  val jsonStr =
    """{
  "DataSourcesMap": {
    "yandex_alb_backend_group": {
      "DeprecationMessage": "",
      "Description": "",
      "Schema": {
        "backend_group_id": {
          "AtLeastOneOf": [],
          "Computed": true,
          "ComputedWhen": [],
          "ConfigMode": 0,
          "ConflictsWith": [],
          "Deprecated": "",
          "Description": "",
          "DiffSuppressOnRefresh": false,
          "ExactlyOneOf": [],
          "ForceNew": false,
          "InputDefault": "",
          "MaxItems": 0,
          "MinItems": 0,
          "Optional": true,
          "Required": false,
          "RequiredWith": [],
          "Sensitive": false,
          "Type": 4
        }
      }
    }
  }
}"""

  val terraformProviderConfig = decode[TerraformProviderConfig](jsonStr)

  terraformProviderConfig match {
    case Right(config) =>
      val caseClassDef = generateCaseClass(config)
      println(s"Generated case class definition:\n$caseClassDef")
    case Left(error) => println(s"Error parsing JSON: $error")
  }
}