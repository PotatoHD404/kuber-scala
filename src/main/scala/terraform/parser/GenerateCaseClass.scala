package terraform.parser

import scala.collection.mutable

case class TypeContext(knownTypes: Map[String, String], generatedClasses: mutable.Set[String] = mutable.Set())

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

  if (context.knownTypes.exists(_._1.contains(className + "("))) {
    context.knownTypes(className)
  } else {
    val newContext = TypeContext(context.knownTypes + (className -> className), context.generatedClasses)
    val fields = resourceData.Schema.toSeq.sortWith(_._1 < _._1).map { field =>
      val fieldName = toCamelCase(field._1)
      val fieldType = generateSchemaField((field._1, field._2), newContext)
      s"  $fieldName: $fieldType"
    }.mkString(",\n")

    val classDef = s"case class $className(\n$fields\n)"
    newContext.generatedClasses += classDef
    className
  }
}

def generateCaseClasses(providerConfig: TerraformProviderConfig): String = {
  val context = TypeContext(Map.empty)

  providerConfig.ResourcesMap.toSeq.sortWith(_._1 < _._1).foreach { resource =>
    generateResourceClass(resource, context)
  }

  providerConfig.DataSourcesMap.toSeq.sortWith(_._1 < _._1).foreach { dataSource =>
    generateResourceClass(dataSource, context)
  }

  generateResourceClass(("Provider", Resource("", "", providerConfig.Schema)), context)

  val generatedClasses = context.generatedClasses.toSeq.sorted.mkString("\n\n")

  s"""
     |// Generated Case Classes
     |$generatedClasses
  """.stripMargin.replace("type:", "`type`:").
    replace("package:", "`package`:").
    replace("class:", "`class`:")
}