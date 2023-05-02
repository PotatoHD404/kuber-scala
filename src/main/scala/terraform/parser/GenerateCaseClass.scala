package terraform.parser

import scala.collection.mutable

def generateCaseClass(providerConfig: TerraformProviderConfig): String = {
  val caseClassBuilder = new mutable.StringBuilder()

  providerConfig.DataSourcesMap.foreach { case (dataSourceName, dataSource) =>
    val className = dataSourceName.split("_").map(_.capitalize).mkString("")
    caseClassBuilder.append(s"case class $className(")

    val fieldDefinitions = dataSource.Schema.map { case (fieldName, field) =>
      val fieldTypeName = field.Type match {
        case 0 => "Boolean"
        case 1 => "Double"
        case 2 => "Int"
        case 3 => "String"
        case 4 => "Long"
        case _ => throw new IllegalArgumentException(s"Unsupported type code: ${field.Type}")
      }
      val fieldType = if (field.Optional) s"Option[$fieldTypeName]" else fieldTypeName
      s"$fieldName: $fieldType"
    }

    caseClassBuilder.append(fieldDefinitions.mkString(", "))
    caseClassBuilder.append(")\n")
  }

  caseClassBuilder.toString()
}