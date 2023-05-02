package terraform

import io.circe.parser.decode
import io.circe.generic.auto.*
import terraform.parser.{TerraformProviderConfig, generateCaseClasses}
@main
def main(): Unit = {
  val jsonStr =
    """{
  "DataSourcesMap": {
    "yandex_alb_backend_group1": {
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
          "Type": 1
        }
      }
    },
    "yandex_alb_backend_group2": {
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
          "Type": 2
        }
      }
    },
    "yandex_alb_backend_group3": {
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
          "Type": 3
        }
      }
    },
    "yandex_alb_backend_group4": {
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
    },
    "yandex_alb_backend_group5": {
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
          "Elem": {
            "AtLeastOneOf": [],
            "Computed": true,
            "ComputedWhen": [],
            "ConfigMode": 0,
            "ConflictsWith": [],
            "Deprecated": "",
            "Description": "",
            "Elem": {
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
              "Type": 1
            },
            "DiffSuppressOnRefresh": false,
            "ExactlyOneOf": [],
            "ForceNew": false,
            "InputDefault": "",
            "MaxItems": 0,
            "MinItems": 0,
            "Optional": false,
            "Required": true,
            "RequiredWith": [],
            "Sensitive": false,
            "Type": 5
          },
          "ExactlyOneOf": [],
          "ForceNew": false,
          "InputDefault": "",
          "MaxItems": 0,
          "MinItems": 0,
          "Optional": true,
          "Required": false,
          "RequiredWith": [],
          "Sensitive": false,
          "Type": 5
        }
      }
    },
    "yandex_alb_backend_group6": {
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
          "Elem": {
            "AtLeastOneOf": [],
            "Computed": true,
            "ComputedWhen": [],
            "ConfigMode": 0,
            "ConflictsWith": [],
            "Deprecated": "",
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
                "Type": 1
              }
            },
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
            "Type": 1
          },
          "ExactlyOneOf": [],
          "ForceNew": false,
          "InputDefault": "",
          "MaxItems": 0,
          "MinItems": 0,
          "Optional": true,
          "Required": false,
          "RequiredWith": [],
          "Sensitive": false,
          "Type": 6
        }
      }
    }
  },
  "ResourcesMap": {},
  "Schema": {}
}"""

  val terraformProviderConfig = decode[TerraformProviderConfig](jsonStr)

  terraformProviderConfig match {
    case Right(config) =>
      val caseClassDef = generateCaseClasses(config)
      println(s"Generated case class definition:\n$caseClassDef")
    case Left(error) => println(s"Error parsing JSON: $error")
  }
}