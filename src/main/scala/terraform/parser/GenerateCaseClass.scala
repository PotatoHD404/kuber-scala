package terraform.parser

import scala.annotation.tailrec
import scala.collection.mutable

case class TypeContext(
                        knownTypes: mutable.Map[String, String] = mutable.Map(),
                        generatedPackages: mutable.Map[String, mutable.ListBuffer[(String, String)]] = mutable.Map()
                      )


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

def generateSchemaField(field: (String, SchemaField), context: TypeContext, packageName: String): String = {
  val (fieldName, schemaField) = field
  val fieldType = generateType(schemaField)

  schemaField.Elem match {
    case Some(Left(resource)) =>
      val (_, className) = generateResourceClass((fieldName, resource), context, packageName, isTopLevel = false)
      fieldType.replace("T", s"$packageName.$className")
    case Some(Right(schemaFieldElem)) =>
      val fieldTypeElem = generateSchemaField((fieldName, schemaFieldElem), context, packageName)
      fieldType.replace("T", fieldTypeElem)
    case None => fieldType
  }
}

def generateResourceClass(resource: (String, TerraformResource), context: TypeContext, packageName: String, isTopLevel: Boolean = true): (String, String) = {
  val (name, resourceData) = resource
  val className = toCamelCase(name).capitalize
  val newPackageName = if (isTopLevel) s"$packageName.${name.toLowerCase}" else packageName

  @tailrec
  def getUniqueClassName(className: String, count: Int = 0): String = {
    val newClassName = if (count > 0) className + count else className
    val existingClass = context.knownTypes.get(newClassName)
    if (existingClass.isEmpty) newClassName
    else getUniqueClassName(className, count + 1)
  }

  val fields = resourceData.Schema.toSeq.sortWith(_._1 < _._1).map { field =>
    val fieldName = toCamelCase(field._1)
    val fieldType = generateSchemaField((field._1, field._2), context, newPackageName)

    if (fieldName.toLowerCase.endsWith("id")) s"  $fieldName: $fieldType /* TODO: add correct ID type */"
    else if (fieldName.toLowerCase.endsWith("ids")) s"  $fieldName: $fieldType /* TODO: add correct IDs type */"
    else if (fieldName.toLowerCase.endsWith("arn")) s"  $fieldName: $fieldType /* TODO: add correct ARN type */"
    else if (fieldName.toLowerCase.endsWith("arns")) s"  $fieldName: $fieldType /* TODO: add correct ARNs type */"
    else if (fieldName.toLowerCase.endsWith("policy")) s"  $fieldName: $fieldType /* TODO: add correct Policy type */"
    else
      s"  $fieldName: $fieldType"
  }.mkString(",\n")

  val uniqueClassName = getUniqueClassName(className)
  val classDef = s"case class $uniqueClassName(\n$fields\n)"

  if (!context.generatedPackages.contains(newPackageName)) {
    context.generatedPackages += (newPackageName -> mutable.ListBuffer((uniqueClassName, classDef)))
  } else {
    context.generatedPackages(newPackageName) += ((uniqueClassName, classDef))
  }

  context.knownTypes += (uniqueClassName -> s"$newPackageName.$uniqueClassName")

  (newPackageName, uniqueClassName)
}

def generateCaseClasses(providerConfig: TerraformProviderConfig, globalPrefix: String): Map[String, List[(String, String)]] = {
  val context = TypeContext()
  providerConfig.ResourcesMap.toSeq.sortWith(_._1 < _._1).foreach { resource =>
    generateResourceClass(resource, context, s"$globalPrefix.resources", isTopLevel = true)
  }

  providerConfig.DataSourcesMap.toSeq.sortWith(_._1 < _._1).foreach { dataSource =>
    generateResourceClass(dataSource, context, s"$globalPrefix.datasources", isTopLevel = true)
  }
  generateResourceClass(("Provider", TerraformResource("", "", providerConfig.Schema)), context, globalPrefix, isTopLevel = true)


  context.generatedPackages.foreach {
    case (packageName, classes) =>
      classes.foreach { case (className, classDef) =>
        println(s"package $packageName\n\n$classDef")
      }
  }


  context.generatedPackages.view.mapValues(_.toList).toMap
}