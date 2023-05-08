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

  val fields = resourceData.Schema.toSeq.sortWith(_._1 < _._1).map { field =>
    val fieldName = toCamelCase(field._1)
    val fieldType = generateSchemaField((field._1, field._2), context)
    s"  $fieldName: $fieldType"
  }.mkString(",\n")

  val classDef = s"case class $className(\n$fields\n)"

  context.generatedClasses.find(isSameClass(_, classDef, className)) match {
    case Some(existingClassName) =>
      existingClassName
    case None =>
      val companionObject = s"""
                               |object $className {
                               |}
      """.stripMargin

      context.generatedClasses += companionObject
      context.generatedClasses += classDef
      context.knownTypes += (className -> classDef)

      // Add implicit conversions between classes with the same fields
      context.generatedClasses.foreach { otherClassDef =>
        if (isSameClass(otherClassDef, classDef, className)) {
          val otherClassName = extractClassName(otherClassDef)
          val conversionMethods =
            s"""
               |  implicit def from$otherClassName(obj: $otherClassName): $className = $className(obj.${fields.split(",\n").map(_.split(":")(0).trim).mkString(", ")})
               |  implicit def to$otherClassName(obj: $className): $otherClassName = $otherClassName(obj.${fields.split(",\n").map(_.split(":")(0).trim).mkString(", ")})
            """.stripMargin
          context.generatedClasses -= otherClassDef
          context.generatedClasses += otherClassDef.replaceFirst("}", conversionMethods + "}")
        }
      }

      className
  }
}

def isSameClass(classDef1: String, classDef2: String, className: String): Boolean = {
  val pattern = s"case class ($className[0-9]*)\\((.*?)\\)".r
  (classDef1, classDef2) match {
    case (pattern(_, fields1), pattern(_, fields2)) => fields1 == fields2
    case _ => false
  }
}

def extractClassName(classDef: String): String = {
  val pattern = s"object (.*?) \\{".r
  classDef match {
    case pattern(name) => name
    case _ => throw new IllegalArgumentException(s"Unable to extract class name from: $classDef")
  }
}

def generateCaseClasses(providerConfig: TerraformProviderConfig): String = {
  val context = TypeContext()
  providerConfig.ResourcesMap.toSeq.sortWith(_._1 < _._1).foreach { resource =>
    generateResourceClass(resource, context)
  }

  generateResourceClass(("Provider", TerraformResource("", "", providerConfig.Schema)), context)

  val generatedClasses = context.generatedClasses.mkString("\n\n")

  s"""
     |// Generated Case Classes
     |$generatedClasses
  """.stripMargin.replace("type:", "`type`:").
    replace("package:", "`package`:").
    replace("class:", "`class`:")
}