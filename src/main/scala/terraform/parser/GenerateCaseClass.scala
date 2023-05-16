package terraform.parser

import scala.annotation.tailrec
import scala.collection.mutable

case class TypeContext(
                        knownTypes: mutable.Map[String, Int] = mutable.Map(),
                        generatedPackages: mutable.Map[String, mutable.ListBuffer[(String, String)]] = mutable.Map()
                      )


def generateType(field: SchemaField, isTopLevel: Boolean): String = {
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
  if (!field.Required && field.RequiredWith.isEmpty && isTopLevel) {
    s"Option[$t]"
  } else {
    t
  }
}

def generateSchemaField(field: (String, SchemaField), context: TypeContext, packageName: String, isTopLevel: Boolean): String = {
  val (fieldName, schemaField) = field
  val fieldType = generateType(schemaField, isTopLevel)



  schemaField.Elem match {
    case Some(Left(resource)) =>
      val (_, className) = generateResourceClass((fieldName, resource), context, packageName, isTopLevel = false)
      fieldType.replace("T", s"$className")
    case Some(Right(schemaFieldElem)) =>
      val fieldTypeElem = generateSchemaField((fieldName, schemaFieldElem), context, packageName, isTopLevel = false)
      fieldType.replace("T", fieldTypeElem)
    case None => fieldType
  }
}
def generateResourceClass(resource: (String, TerraformResource), context: TypeContext, packageName: String, isTopLevel: Boolean = true): (String, String) = {
  val (name, resourceData) = resource
  val className = toCamelCase(name).capitalize
  val newPackageName = if (isTopLevel) s"$packageName.${name.toLowerCase}" else packageName


  val fields = resourceData.Schema.toSeq.sortWith(_._1 < _._1).map { field =>
    val fieldName = toCamelCase(field._1)
    val fieldType = generateSchemaField((field._1, field._2), context, newPackageName, isTopLevel)
    s"  $fieldName: $fieldType"
  }.mkString(",\n")

  @tailrec
  def getUniqueClassName(className: String, count: Int = 0): String = {
    val newClassName = if (count > 0) className + count else className
    val existingClass = context.knownTypes.contains(s"$newPackageName.$newClassName")
    if (!existingClass) newClassName
    else if (context.knownTypes(s"$newPackageName.$newClassName") == fields.hashCode()) newClassName
    else getUniqueClassName(className, count + 1)
  }

  val uniqueClassName = getUniqueClassName(className)
  val classDef = s"case class $uniqueClassName(\n$fields\n)"
  val classDefHash = fields.hashCode()

  if (!context.knownTypes.contains(s"$newPackageName.$uniqueClassName")) {
    context.knownTypes += (s"$newPackageName.$uniqueClassName" -> classDefHash)
    val packageClasses = context.generatedPackages.getOrElseUpdate(newPackageName, mutable.ListBuffer())
    packageClasses += ((uniqueClassName, classDef))
  }

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