package terraform.parser

import scala.collection.mutable

case class TypeContext(knownTypes: Map[String, String])

def toCamelCase(str: String): String = {
  "_([a-z\\d])".r.replaceAllIn(str, _.group(1).toUpperCase())
}

def generateType(field: SchemaField): String = {
  val fieldType = field.Type
  val t = fieldType match {
    case 1 => "Boolean"
    case 2 => "Int"
    case 3 => "Double"
    case 4 => "String"
    case 5 => "List[T]"
    case 6 => "Map[String, T]"
    case 7 => "Set[T]"
    case _ => throw new IllegalArgumentException(s"Unsupported type code: $fieldType")
  }
  if (!field.Required && field.RequiredWith.isEmpty) {
    s"Option[$t]"
  } else {
    t
  }
}

def generateSchemaField(field: (String, SchemaField), context: TypeContext): String = {
  val (fieldName, schemaField) = field
  val fieldType = generateType(schemaField)

  schemaField.Elem match {
    case Some(Left(resource)) =>
      val resourceClass = generateResourceClass((fieldName, resource), context)
      fieldType.replace("T", resourceClass)
    case Some(Right(schemaFieldElem)) =>
      val nestedField = generateSchemaField((fieldName, schemaFieldElem), context)
      fieldType.replace("T", nestedField)
    case None => fieldType
  }
}

def generateResourceClass(resource: (String, Resource), context: TypeContext): String = {
  val (name, resourceData) = resource
  val className = toCamelCase(name).capitalize

  if (context.knownTypes.contains(className)) {
    context.knownTypes(className)
  } else {
    val newContext = TypeContext(context.knownTypes + (className -> className))
    val fields = resourceData.Schema.toSeq.sortWith(_._1 < _._1).map { field =>
      val fieldName = toCamelCase(field._1)
      val fieldType = generateSchemaField((field._1, field._2), newContext)
      s"  $fieldName: $fieldType"
    }.mkString(",\n")

    s"case class $className(\n$fields\n)"
  }
}

def generateCaseClasses(providerConfig: TerraformProviderConfig): String = {
  val context = TypeContext(Map.empty)


  val resources = providerConfig.ResourcesMap.toSeq.sortWith(_._1 < _._1).map { resource =>
    generateResourceClass(resource, context)
  }.mkString("\n\n")

  val dataSources = providerConfig.DataSourcesMap.toSeq.sortWith(_._1 < _._1).map { dataSource =>
    generateResourceClass(dataSource, context)
  }.mkString("\n\n")

  val schema = providerConfig.Schema.toSeq.sortWith(_._1 < _._1).map(field => generateSchemaField(field, context)).mkString("\n\n")

  s"""
     |// Resources
     |$resources
     |
     |// Data Sources
     |$dataSources
     |
     |// Provider Schema
     |$schema
  """.stripMargin
}