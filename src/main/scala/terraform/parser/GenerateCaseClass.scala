package terraform.parser

import scala.annotation.tailrec
import scala.collection.mutable

case class TypeContext(knownTypes: mutable.Map[String, String] = mutable.Map(), generatedClasses: mutable.Set[String] = mutable.Set())

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

def generateResourceClass(resource: (String, TerraformResource), context: TypeContext): String = {
  val (name, resourceData) = resource
  val className = toCamelCase(name).capitalize

  @tailrec
  def getUniqueClassName(className: String, count: Int = 0): String = {
    val newClassName = if (count > 0) className + count else className
    val existingClass = context.knownTypes.get(newClassName)
    //    if (existingClass.isEmpty) newClassName
    //    val existingClass = context.generatedClasses.find(_.startsWith(s"case class $newClassName"))
    if (existingClass.isEmpty) newClassName
    else getUniqueClassName(className, count + 1)
  }

  def isSameClass(classDef1: String, classDef2: String, className: String): Boolean = {
    val pattern = s"case class $className[0-9]*\\((.*?)\\)".r
    (classDef1, classDef2) match {
      case (pattern(fields1), pattern(fields2)) => fields1 == fields2
      case _ => false
    }
  }

  //  val uniqueClass = uniqueClassName(className)
  val fields = resourceData.Schema.toSeq.sortWith(_._1 < _._1).map { field =>
    val fieldName = toCamelCase(field._1)
    val fieldType = generateSchemaField((field._1, field._2), context)

    if (fieldName.toLowerCase.endsWith("id")) s"  $fieldName: $fieldType // TODO: add correct ID type"
    else if (fieldName.toLowerCase.endsWith("ids")) s"  $fieldName: $fieldType // TODO: add correct IDs type"
    else if (fieldName.toLowerCase.endsWith("arn")) s"  $fieldName: $fieldType // TODO: add correct ARN type"
    else if (fieldName.toLowerCase.endsWith("arns")) s"  $fieldName: $fieldType // TODO: add correct ARNs type"
    else if (fieldName.toLowerCase.endsWith("policy")) s"  $fieldName: $fieldType // TODO: add correct Policy type"
    else
      s"  $fieldName: $fieldType"
  }.mkString(",\n")
  var classDef = s"case class $className(\n$fields\n)"
  if (context.generatedClasses.exists(isSameClass(_, classDef, className))) {
    className
  } else {
    val uniqueClassName = getUniqueClassName(className)

    classDef = s"case class $uniqueClassName(\n$fields\n)"


    context.knownTypes += (uniqueClassName -> classDef)
    context.generatedClasses += classDef

    uniqueClassName
  }
}

def generateCaseClasses(providerConfig: TerraformProviderConfig): String = {
  val context = TypeContext()
  providerConfig.ResourcesMap.toSeq.sortWith(_._1 < _._1).foreach { resource =>
    generateResourceClass(resource, context)
  }

  providerConfig.DataSourcesMap.toSeq.sortWith(_._1 < _._1).foreach { dataSource =>
    generateResourceClass(dataSource, context)
  }

  generateResourceClass(("Provider", TerraformResource("", "", providerConfig.Schema)), context)

  val generatedClasses = context.generatedClasses.toSeq.sorted.mkString("\n\n")

  s"""
     |// Generated Case Classes
     |$generatedClasses
  """.stripMargin.replace("type:", "`type`:").
    replace("package:", "`package`:").
    replace("class:", "`class`:")
}