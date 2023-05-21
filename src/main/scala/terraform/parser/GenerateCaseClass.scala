package terraform.parser

import scala.annotation.tailrec
import scala.collection.mutable
import terraform.HCLImplicits._

val resourceReferences: Map[String, String] = Map(
  "yandex_vpc_subnet.network_id" -> "yandex_vpc_network.id"
  // Add more mappings here as needed...
)

case class TypeContext(
                        knownTypes: mutable.Map[String, Int] = mutable.Map(),
                        generatedPackages: mutable.Map[String, mutable.ListBuffer[(String, String)]] = mutable.Map()
                      )

def generateType(field: SchemaField, fieldName: String): String = {
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
  val mappedType = resourceReferences.getOrElse(fieldName, t)

  if (!field.Required && field.RequiredWith.isEmpty) {
    s"Option[$mappedType]"
  } else {
    mappedType
  }
}

def generateSchemaField(field: (String, SchemaField), context: TypeContext, packageName: String, providerName: String): String = {
  val (fieldName, schemaField) = field
  val fieldType = generateType(schemaField, s"$packageName.$fieldName")

  schemaField.Elem match {
    case Some(Left(resource)) =>
      val (_, className) = generateResourceClass((fieldName, resource), context, packageName, "", providerName)
      fieldType.replace("T", className)
    case Some(Right(schemaFieldElem)) =>
      val fieldTypeElem = generateSchemaField((fieldName, schemaFieldElem.copy(Required = true)), context, packageName, providerName)
      fieldType.replace("T", fieldTypeElem)
    case None => fieldType
  }
}

def generateResourceClass(
                           resource: (String, TerraformResource),
                           context: TypeContext,
                           packageName: String,
                           classType: String,
                           providerName: String
                         ): (String, String) = {
  val (name, resourceData) = resource
  val className = toCamelCase(name).capitalize
  val newPackageName = if (classType != "") s"$packageName.${name.toLowerCase}" else packageName

  val fields = ((
    classType match {
      case "resource" => Some(s"  resourceName: String")
      case "datasource" => Some(s"  datasourceName: String")
      case _ => None
    }) ++: resourceData.Schema.toSeq.sortWith(_._1 < _._1).map { field =>
    val fieldName = toCamelCase(field._1)
    val fieldType = generateSchemaField((s"$packageName.$fieldName", field._2), context, newPackageName, providerName)
    s"  $fieldName: $fieldType"
  }).mkString(",\n")

  @tailrec
  def getUniqueClassName(className: String, count: Int = 0): String = {
    val newClassName = if (count > 0) className + count else className
    val existingClass = context.knownTypes.contains(s"$newPackageName.$newClassName")
    if (!existingClass) newClassName
    else if (context.knownTypes(s"$newPackageName.$newClassName") == fields.hashCode()) newClassName
    else getUniqueClassName(className, count + 1)
  }

  val uniqueClassName = getUniqueClassName(className)

  val toHCLBody = resourceData.Schema.toSeq.sortWith(_._1 < _._1).map { field =>
    val fieldName = toCamelCase(field._1)
    val realFieldName = field._1
    val isOptional = !field._2.Required && field._2.RequiredWith.isEmpty
    field._2.Type match {
      case 1 | 2 | 3 | 4 if isOptional => s"""    this.$fieldName.map(_.toHCL).map(el => s"$realFieldName = $${el}")"""
      case 5 if isOptional => s"""    this.$fieldName.map(_.map(_.toHCL).mkString(", ")).map(el => s"$realFieldName = [$${el}]")"""
      case 6 if isOptional => s"""    this.$fieldName.map(_.view.mapValues(_.toHCL).mkString(", ")).map(el => s"$realFieldName = {$${el}}")"""
      case 7 if isOptional => s"""    this.$fieldName.map(_.map(_.toHCL).mkString(", ")).map(el => s"$realFieldName = [$${el}]")"""
      case 1 | 2 | 3 | 4 => s"""    Some(s"$realFieldName = $${this.$fieldName}")"""
      case 5 => s"""    Some(s"$realFieldName = [$${this.$fieldName.map(_.toHCL).mkString(", ")}]")"""
      case 6 => s"""    Some(s"$realFieldName = {$${this.$fieldName.view.mapValues(_.toHCL).mkString(", ")}}")"""
      case 7 => s"""    Some(s"$realFieldName = [$${this.$fieldName.map(_.toHCL).mkString(", ")}]")"""
      case _ => throw new IllegalArgumentException(s"Unsupported type code: ${field._2.Type}")
    }
  }.mkString(",\n")

  val toHCLMethod =
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
  val classDef = classType match {
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

  val classDefHash = fields.hashCode()

  if (!context.knownTypes.contains(s"$newPackageName.$uniqueClassName")) {
    context.knownTypes += (s"$newPackageName.$uniqueClassName" -> classDefHash)
    val packageClasses = context.generatedPackages.getOrElseUpdate(newPackageName, mutable.ListBuffer())
    packageClasses += ((uniqueClassName, classDef))
  }

  (newPackageName, uniqueClassName)
}

def generateCaseClasses(providerConfig: TerraformProviderConfig, globalPrefix: String, providerName: String): Map[String, List[(String, String)]] = {
  val context = TypeContext()

  providerConfig.ResourcesMap.toSeq.sortWith(_._1 < _._1).foreach { resource =>
    generateResourceClass(resource, context, s"$globalPrefix.resources", "resource", providerName)
  }

  providerConfig.DataSourcesMap.toSeq.sortWith(_._1 < _._1).foreach { dataSource =>
    generateResourceClass(dataSource, context, s"$globalPrefix.datasources", "datasource", providerName)
  }

  generateResourceClass(
    (s"${providerName}ProviderSettings", TerraformResource("", "", providerConfig.Schema)),
    context,
    globalPrefix,
    "provider",
    providerName
  )



  context.generatedPackages.map {
    case (packageName, classes) =>
      packageName -> classes.map { case (className, classDef) =>
        (className, s"""package $packageName
        |
        |import terraform.HCLImplicits._
        |import $globalPrefix._
        |import terraform.{InfrastructureResource, ProviderSettings, ProviderType, BackendResource}
        |
        |$classDef"""
          .stripMargin
          .replace("type:", "`type`:")
          .replace(".type", ".`type`")
          .replace("package:", "`package`:")
          .replace(".package", ".`package`")
          .replace("class:", "`class`:")
          .replace(".class", ".`class`"))
      }
  }.view.mapValues(_.toList).toMap +  (globalPrefix -> List((providerName,
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
       |  T3](provider, backend, resources)""".stripMargin)))
}
