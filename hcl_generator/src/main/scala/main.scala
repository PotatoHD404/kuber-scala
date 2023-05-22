import io.circe.Decoder
import io.circe.jawn.decode
import io.circe.parser.parse

import scala.io.Source
import scala.util.Try

sealed trait JsonValue

case class JsonString(value: String) extends JsonValue

case class JsonBool(value: Boolean) extends JsonValue

case class JsonLong(value: Long) extends JsonValue

case class JsonDouble(value: Double) extends JsonValue

implicit val decodeJson: Decoder[JsonValue] = List[Decoder[JsonValue]](
  Decoder[String].map(JsonString.apply),
  Decoder[Boolean].map(JsonBool.apply),
  Decoder[Long].map(JsonLong.apply),
  Decoder[Double].map(JsonDouble.apply)
).reduceLeft(_ or _)

type JsonMap = Map[String, List[JsonValue]]

@main
def main(): Unit = {
  import scala.util.matching.Regex
  // Assuming you have the JSON string

  val source = Source.fromFile("./terraform-docs-extractor/results/yandex.json")
  val jsonString = source.getLines().mkString
  source.close()

  // Decode the JSON string into Map[String, List[String]]
  val map = decode[JsonMap](jsonString) match {
    case Right(jsonMap) => jsonMap
    case Left(error) => throw new RuntimeException(error.getMessage)
  }

  // Define the pattern
  val fieldPattern: Regex = """^(:?data\.)?yandex_[\w._-]+\.[\w._-]+\.[\w._-]+$""".r
  val domainPattern: Regex = """^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\-]*[a-zA-Z0-9])\.)*([A-Za-z]|[A-Za-z][A-Za-z0-9\-]*[A-Za-z0-9])$""".r
  val ipPattern: Regex = """^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$""".r
  val ipMaskPattern: Regex = """^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)/([0-9]|[1-2][0-9]|3[0-2])$""".r

  // Flatten the map values, get distinct values
  val distinctFlatPairs: List[(String, String)] = map.flatMap { case (key, valueList) =>
    valueList.collect {
      case JsonString(s) => (key, s)
    }
  }.toList.distinct

  // Filter out values that are field links
  val fieldValues: List[(String, String)] = distinctFlatPairs.filter { case (_, value) =>
    value.startsWith("${") && value.endsWith("}") || fieldPattern.matches(value)
  }

  val otherValues: JsonMap = map.filterNot { case (key, _) =>
    fieldValues.exists(_._1 == key)
  }

  val modifiedFieldValues: List[(String, String)] = fieldValues.collect {
    case pair@(key, value) if value.startsWith("${") && value.endsWith("}") || fieldPattern.matches(value) =>
      (key, value.stripPrefix("${").stripSuffix("}"))
  }.collect {
    case (key, value) =>
      val splitValue = value.split("\\.").toList
      val addition = if key.startsWith("data.") then 1 else 0
      (key, (splitValue.take(1 + addition) ++ splitValue.drop(2 + addition)).mkString("."))
  }.collect {
    case (key, value) =>
      val splitKey = key.split("\\.").toList
      val addition = if key.startsWith("resource.") || key.startsWith("data.") then 1 else 0
      ((splitKey.take(1 + addition) ++ splitKey.drop(2 + addition)).mkString("."), value)
  }

  def filterAndCollectValues(map: JsonMap, condition: String => Boolean): List[(String, String)] = {
    map.flatMap { case (key, valueList) =>
      valueList.collect {
        case JsonString(s) if condition(s) => (key, s)
      }
    }.toList
  }

  val domainValues: List[(String, String)] = filterAndCollectValues(otherValues, domainPattern.matches)
  val ipValues: List[(String, String)] = filterAndCollectValues(otherValues, ipPattern.matches)
  val ipMaskValues: List[(String, String)] = filterAndCollectValues(otherValues, ipMaskPattern.matches)
  val jsonValues: List[(String, String, String)] = otherValues.flatMap { case (key, valueList) =>
    valueList.collect {
      case JsonString(s) if parse(s).isRight =>
        val json = parse(s).getOrElse(null)
        if (json.isBoolean) (key, s, "Boolean")
        else if (json.isString) (key, s, "String")
        else if (json.isNumber) {
          Try(json.asNumber.get.toLong.getOrElse(json.asNumber.get.toDouble))
            .map(value => if (value.isInstanceOf[Long]) (key, s, "Long") else (key, s, "Double"))
            .getOrElse((key, s, "Unknown"))
        } else if (json.isArray) (key, s, "Array")
        else if (json.isObject) (key, s, "Object")
        else (key, s, "Unknown")
    }
  }.toList

  // Print the values
  println("\nDomains:")
  domainValues.foreach { case (key, s) => println(s"$key: $s") }
  println("\nIPs:")
  ipValues.foreach { case (key, s) => println(s"$key: $s") }
  println("\nIP Masks:")
  ipMaskValues.foreach { case (key, s) => println(s"$key: $s") }
  println("\nJSON Strings:")
  jsonValues.foreach { case (key, s, valueType) => println(s"$key: $s ($valueType)") }
  println("\nField Links:")
  modifiedFieldValues.foreach { case (key, s) => println(s"$key: $s") }
}
