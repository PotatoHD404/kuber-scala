package terraform.parser

import scala.annotation.tailrec
import scala.collection.mutable
import terraform.HCLImplicits._

// Add string wraps
// opaque types
// newType


object TypeCodes {
  val TYPE_BOOLEAN = 1
  val TYPE_INT = 2
  val TYPE_DOUBLE = 3
  val TYPE_STRING = 4
  val TYPE_LIST = 5
  val TYPE_MAP = 6
  val TYPE_SET = 7
}

import TypeCodes._

case class TypeContext(
                        knownTypes: mutable.Map[String, Int] = mutable.Map(),
                        generatedPackages: mutable.Map[String, mutable.ListBuffer[(String, String)]] = mutable.Map(),
                        opaqueTypesGenerated: mutable.Map[String, Set[String]] = mutable.Map()
                      )

def generateFieldType(field: SchemaField): String = {
  val fieldType = field.Type
  val t = fieldType match {
    case TYPE_BOOLEAN => "Boolean"
    case TYPE_INT => "Int"
    case TYPE_DOUBLE => "Double"
    case TYPE_STRING => "String"
    case TYPE_LIST => "List[T]"
    case TYPE_MAP => "Map[String, T]"
    case TYPE_SET => "Set[T]"
    case _ => throw new IllegalArgumentException(s"Unsupported type code: $fieldType")
  }
  if (!field.Required && field.RequiredWith.isEmpty) s"Option[$t]" else t
}

def updateFieldWithType(field: (String, SchemaField), parsedDocs: DocsInfo, fieldType: String, fullClassName: String): String = {
  val newFullName = appendFieldNameToClassName(fullClassName, field._1)
  parsedDocs.fieldLinks.get(newFullName) match {
    case Some(linkedField) => s"${linkedField.split("\\.").last.capitalize}Type"
    case None => fieldType
  }
}

def appendFieldNameToClassName(fullClassName: String, fieldName: String): String = {
  val splitName = fullClassName.split("\\.")
  if (splitName.lastOption.contains(fieldName)) fullClassName else fullClassName + "." + fieldName
}

// other functions remain the same...

def generateTodoComment(fieldName: String, parsedDocs: DocsInfo): String = {
  //  println(fieldName)
  if (parsedDocs.domains.contains(fieldName)) "TODO: Check if this domain field type is correct."
  else if (parsedDocs.ips.contains(fieldName)) "TODO: Check if this IP field type is correct."
  else if (parsedDocs.ipMasks.contains(fieldName)) "TODO: Check if this IP mask field type is correct."
  else if (parsedDocs.jsonStrings.contains(fieldName)) "TODO: Check if this JSON String field type is correct."
  else ""
}

def generateSchemaForElem(field: (String, SchemaField), fieldType: String,
                          context: TypeContext, packageName: String, providerName: String,
                          newFullName: String, parsedDocs: DocsInfo): String = {
  field._2.Elem match {
    case Some(Left(resource)) =>
      val (_, className) = generateResourceClass((field._1, resource), context, packageName, "", providerName, newFullName, parsedDocs)
      fieldType.replace("T", className)
    case Some(Right(schemaFieldElem)) =>
      val fieldTypeElem = generateSchemaField((field._1, schemaFieldElem.copy(Required = true)), context, packageName, providerName, newFullName, parsedDocs)
      fieldType.replace("T", fieldTypeElem)
    case None =>
      val todoComment = generateTodoComment(newFullName, parsedDocs)
      if (todoComment.isEmpty) fieldType else s"$fieldType /* $todoComment */"
  }
}

def generateSchemaField(field: (String, SchemaField),
                        context: TypeContext,
                        packageName: String,
                        providerName: String,
                        fullClassName: String,
                        parsedDocs: DocsInfo
                       ): String = {
  val (fieldName, schemaField) = field
  val fieldType = generateFieldType(schemaField)
  val splitName = fullClassName.split("\\.")
  val newFullName = if (splitName.lastOption.contains(fieldName)) fullClassName else fullClassName + "." + fieldName
  val finalFieldType = parsedDocs.fieldLinks.get(newFullName) match {
    case Some(linkedField) => s"${linkedField.split("\\.").last.capitalize}Type"
    case None => fieldType
  }
  generateSchemaForElem(field, finalFieldType, context, packageName, providerName, newFullName, parsedDocs)
}

def generateResourceClassName(name: String): String =
  toCamelCase(name).capitalize

def generatePackageName(packageName: String, classType: String, name: String): String =
  if (classType != "") s"$packageName.${name.toLowerCase}" else packageName

def generateFullName(fullClassName: String, name: String): String =
  fullClassName + "." + name.toLowerCase

def generateFields(classType: String, resourceData: TerraformResource, context: TypeContext, newPackageName: String, providerName: String, newFullName: String, parsedDocs: DocsInfo): String = {
  ((classType match {
    case "resource" => Some(s"  resourceName: String")
    case "datasource" => Some(s"  datasourceName: String")
    case _ => None
  }) ++: resourceData.Schema.toSeq.sortWith(_._1 < _._1).map { field =>
    val fieldName = toCamelCase(field._1)
    val fieldType = generateSchemaField((field._1, field._2), context, newPackageName, providerName, newFullName, parsedDocs)
    s"  $fieldName: $fieldType"
  }).mkString(",\n")
}

@tailrec
def getUniqueClassName(className: String, newPackageName: String, fields: String, context: TypeContext, count: Int = 0): String = {
  val newClassName = if (count > 0) className + count else className
  val existingClass = context.knownTypes.contains(s"$newPackageName.$newClassName")
  if (!existingClass) newClassName
  else if (context.knownTypes(s"$newPackageName.$newClassName") == fields.hashCode()) newClassName
  else getUniqueClassName(className, newPackageName, fields, context, count + 1)
}

def generateToHCLBody(resourceData: TerraformResource) = {
  resourceData.Schema.toSeq.sortWith(_._1 < _._1).map { field =>
    val fieldName = toCamelCase(field._1)
    val realFieldName = field._1
    val isOptional = !field._2.Required && field._2.RequiredWith.isEmpty
    field._2.Type match {
      case TYPE_BOOLEAN | TYPE_INT | TYPE_DOUBLE | TYPE_STRING if isOptional => s"""    this.$fieldName.map(_.toHCL).map(el => s"$realFieldName = $${el}")"""
      case TYPE_LIST if isOptional => s"""    this.$fieldName.map(_.map(_.toHCL).mkString(", ")).map(el => s"$realFieldName = [$${el}]")"""
      case TYPE_MAP if isOptional => s"""    this.$fieldName.map(_.view.mapValues(_.toHCL).mkString(", ")).map(el => s"$realFieldName = {$${el}}")"""
      case TYPE_SET if isOptional => s"""    this.$fieldName.map(_.map(_.toHCL).mkString(", ")).map(el => s"$realFieldName = [$${el}]")"""
      case TYPE_BOOLEAN | TYPE_INT | TYPE_DOUBLE | TYPE_STRING => s"""    Some(s"$realFieldName = $${this.$fieldName}")"""
      case TYPE_LIST => s"""    Some(s"$realFieldName = [$${this.$fieldName.map(_.toHCL).mkString(", ")}]")"""
      case TYPE_MAP => s"""    Some(s"$realFieldName = {$${this.$fieldName.view.mapValues(_.toHCL).mkString(", ")}}")"""
      case TYPE_SET => s"""    Some(s"$realFieldName = [$${this.$fieldName.map(_.toHCL).mkString(", ")}]")"""
      case _ => throw new IllegalArgumentException(s"Unsupported type code: ${field._2.Type}")
    }
  }.mkString(",\n")
}
def generateToHCLMethod(classType: String, name: String, toHCLBody: String) = {
  s"""
     |  def toHCL: String = s"${
    classType match {
      case "resource" => s"""resource \\"$name\\" \\"$${this.resourceName}\\""""
      case "datasource" => s"""data \\"$name\\" \\"$${this.datasourceName}\\""""
      case "provider" => s"""provider \\"$name\\""""
      case _ => ""
    }
  }{" +
     |    List[Option[String]](
     |$toHCLBody
     |    ).flatten.mkString("\\n")
     |    + "}"
     |""".stripMargin
}
def generateFieldDescriptions(resourceData: TerraformResource) = {
  resourceData.Schema.toSeq.sortWith(_._1 < _._1).map { field =>
    val fieldName = toCamelCase(field._1)
    val fieldDescription = field._2.Description.trim
    val fieldDepMsg = field._2.Deprecated.trim
    val fieldDocList = List(
      if (fieldDescription.nonEmpty) Some(fieldDescription) else None,
      if (fieldDepMsg.nonEmpty) Some(s"Deprecated: $fieldDepMsg") else None
    ).flatten
    if (fieldDocList.nonEmpty) {
      val fieldDocStr = fieldDocList.mkString(". ")
      s" * @param $fieldName $fieldDocStr"
    } else ""
  }.filter(_.nonEmpty).mkString("\n")
}
def generateClassDoc(resourceData: TerraformResource, fieldDescriptions: String) = {

  val desc = resourceData.Description.trim
  val depMsg = resourceData.DeprecationMessage.trim
  val docList = List(
    if (desc.nonEmpty) Some(s" * $desc") else None,
    if (depMsg.nonEmpty) Some(s" * @deprecated $depMsg") else None,
    if (fieldDescriptions.nonEmpty) Some(fieldDescriptions) else None
  ).flatten
  if (docList.nonEmpty) {
    val docStr = docList.mkString("\n")
    s"""/**
       |$docStr
       | */\n""".stripMargin
  } else ""

}
def generateDeprecationAnnotation(resourceData: TerraformResource) = {
  if (resourceData.DeprecationMessage.nonEmpty) {
    s"""@deprecated("${resourceData.DeprecationMessage}", "")\n"""
  } else ""
}
def generateClassDef(classType: String, providerName: String, fields: String, uniqueClassName: String, toHCLMethod: String) = {
  classType match {
    case "resource" | "datasource" =>
      s"""case class $uniqueClassName(\n$fields\n) extends InfrastructureResource[$providerName] {
         |$toHCLMethod
         |}""".stripMargin
    case "provider" =>
      s"""case class $uniqueClassName(\n$fields\n) extends ProviderSettings[$providerName] {
         |$toHCLMethod
         |}""".stripMargin
    case "backend" =>
      s"""case class $uniqueClassName(\n$fields\n) extends BackendResource {
         |$toHCLMethod
         |}""".stripMargin
    case "" =>
      s"""case class $uniqueClassName(\n$fields\n) {
         |$toHCLMethod
         |}""".stripMargin

    case _ => throw new IllegalArgumentException(s"Unsupported class type: $classType")
  }
}

def generatePackageCode(uniqueClassName: String, fullClassDef: String, linkedFields: String, linkedFieldImports: String) = {
  val companionObject = if (linkedFields.nonEmpty) {
    s"""
       |object $uniqueClassName {
       |$linkedFields
       |}
       |""".stripMargin
  } else ""

  s"""$linkedFieldImports
     |
     |$companionObject
     |$fullClassDef
     |""".stripMargin
}

def updateContext(context: TypeContext, newPackageName: String, uniqueClassName: String, packageCode: String, classDefHash: Int) = {
  if (!context.knownTypes.contains(s"$newPackageName.$uniqueClassName")) {
    context.knownTypes += (s"$newPackageName.$uniqueClassName" -> classDefHash)
    val packageClasses = context.generatedPackages.getOrElseUpdate(newPackageName, mutable.ListBuffer())
    packageClasses += ((uniqueClassName, packageCode))
  }
}
def generateLinkedFields(resourceData: TerraformResource, newFullName: String, parsedDocs: DocsInfo, context: TypeContext) = {
  resourceData.Schema.keys.flatMap { fieldName => {
    val linkedField = newFullName + "." + fieldName
    if parsedDocs.fieldLinksSet.contains(linkedField) then {
      val opaqueType = toCamelCase(linkedField.split("\\.").last).capitalize + "Type"
      // Check if the opaque type was already created for the datasource.
      if (context.opaqueTypesGenerated.get(newFullName).contains(opaqueType)) {
        None
      } else {
        println(s"Generating opaque type $opaqueType for $newFullName.$fieldName")
        // Add the new opaque type to the generated types for the current class type.
        context.opaqueTypesGenerated.updateWith(newFullName) {
          case Some(opaqueTypes) => Some(opaqueTypes + opaqueType)
          case None => Some(Set(opaqueType))
        }
        println(context.opaqueTypesGenerated)
        Some(s"  opaque type $opaqueType = String")
      }
    } else {
      None
    }
  }
  }.mkString("\n")
}
def generateResourceClass(resource: (String, TerraformResource),
                          context: TypeContext,
                          packageName: String,
                          classType: String,
                          providerName: String,
                          fullClassName: String,
                          parsedDocs: DocsInfo
                         ): (String, String) = {
  val (name, resourceData) = resource
  val className = generateResourceClassName(name)
  val newPackageName = generatePackageName(packageName, classType, name)
  val newFullName = generateFullName(fullClassName, name)
  val fields = generateFields(classType, resourceData, context, newPackageName, providerName, newFullName, parsedDocs)

  val uniqueClassName = getUniqueClassName(className, newPackageName, fields, context)

  val toHCLBody = generateToHCLBody(resourceData)

  val toHCLMethod =
    generateToHCLMethod(classType, name, toHCLBody)

  // Generate field descriptions for ScalaDoc.
  val fieldDescriptions = generateFieldDescriptions(resourceData)

  // Generate the class documentation, removing redundant lines.
  val classDoc = generateClassDoc(resourceData, fieldDescriptions)

  // Generate the deprecation annotation, if needed.
  val deprecationAnnotation = generateDeprecationAnnotation(resourceData)

  val classDef = generateClassDef(classType, providerName, fields, uniqueClassName, toHCLMethod)

  val fullClassDef = s"$classDoc$deprecationAnnotation$classDef"

  // Generate the opaque type for the linked field
  val linkedFields = generateLinkedFields(resourceData, newFullName, parsedDocs, context)

  // Generate the necessary imports for linked fields
  val linkedFieldImports = generateLinkedFieldImports(newFullName, parsedDocs)

  val packageCode = generatePackageCode(uniqueClassName, fullClassDef, linkedFields, linkedFieldImports)

  val classDefHash = fields.hashCode()

  updateContext(context, newPackageName, uniqueClassName, packageCode, classDefHash)

  (newPackageName, uniqueClassName)
}

def generateLinkedFieldImports(newFullName: String, parsedDocs: DocsInfo): String = {
  parsedDocs.fieldLinks.collect {
    case (linkedField, sourceField) if linkedField.startsWith(newFullName) =>
      val sourceClassName = sourceField.split("\\.").dropRight(1).mkString(".")
      s"import $sourceClassName.${sourceField.split("\\.").last.capitalize}Type"
  }.mkString("\n")
}

def generateCaseClasses(providerConfig: TerraformProviderConfig, globalPrefix: String, providerName: String, parsedDocs: DocsInfo): Map[String, List[(String, String)]] = {
  val context = TypeContext()

  generateDataSources(providerConfig, globalPrefix, providerName, parsedDocs, context)
  generateResources(providerConfig, globalPrefix, providerName, parsedDocs, context)
  generateProviderResource(providerConfig, globalPrefix, providerName, parsedDocs, context)

  printFieldLinksInfo(parsedDocs, context)

  val generatedPackages = generatePackages(context, globalPrefix)
  val providerPackage = generateProviderPackage(globalPrefix, providerName)

  generatedPackages + providerPackage
}

def generateDataSources(providerConfig: TerraformProviderConfig, globalPrefix: String, providerName: String, parsedDocs: DocsInfo, context: TypeContext): Unit = {
  providerConfig.DataSourcesMap.toSeq.sortWith(_._1 < _._1).foreach { dataSource =>
    generateResourceClass(dataSource, context, s"$globalPrefix.datasources", "datasource", providerName, "", parsedDocs)
  }
}

def generateResources(providerConfig: TerraformProviderConfig, globalPrefix: String, providerName: String, parsedDocs: DocsInfo, context: TypeContext): Unit = {
  providerConfig.ResourcesMap.toSeq.sortWith(_._1 < _._1).foreach { resource =>
    generateResourceClass(resource, context, s"$globalPrefix.resources", "resource", providerName, "", parsedDocs)
  }
}

def generateProviderResource(providerConfig: TerraformProviderConfig, globalPrefix: String, providerName: String, parsedDocs: DocsInfo, context: TypeContext): Unit = {
  generateResourceClass(
    (s"${providerName}ProviderSettings", TerraformResource("", "", providerConfig.Schema)),
    context,
    globalPrefix,
    "provider",
    providerName,
    "",
    parsedDocs
  )
}

def printFieldLinksInfo(parsedDocs: DocsInfo, context: TypeContext): Unit = {
  println(s"Field links count: ${parsedDocs.fieldLinksSet.size}")
  println(s"Field links really added: ${context.opaqueTypesGenerated.map(_._2.size).sum}")
}

def generatePackages(context: TypeContext, globalPrefix: String): Map[String, List[(String, String)]] = {
  context.generatedPackages.map {
    case (packageName, classes) =>
      packageName -> classes.map { case (className, classDef) =>
        (className,
          s"""package $packageName
             |
             |import terraform.HCLImplicits._
             |import $globalPrefix._
             |import terraform.{InfrastructureResource, ProviderSettings, ProviderType, BackendResource}
             |
             |$classDef"""
            .stripMargin.escapeKeywords)
      }
  }.view.mapValues(_.toList).toMap
}

def generateProviderPackage(globalPrefix: String, providerName: String): (String, List[(String, String)]) = {
  globalPrefix -> List((providerName,
    s"""package $globalPrefix
       |
       |import terraform.{InfrastructureResource, ProviderSettings, ProviderType, BackendResource, ProviderConfig}
       |
       |sealed trait $providerName extends ProviderType
       |
       |class ${providerName}ProviderConfig[
       |  T1 <: ProviderSettings[$providerName],
       |  T2 <: BackendResource,
       |  T3 <: InfrastructureResource[$providerName]
       |  ](provider: T1, backend: Option[T2], resources: List[T3]) extends ProviderConfig[
       |  $providerName,
       |  T1,
       |  T2,
       |  T3](provider, backend, resources)""".stripMargin))
}