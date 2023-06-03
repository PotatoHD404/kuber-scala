package terraform.parser

import io.circe.*
import io.circe.parser.*

import scala.io.Source
import scala.util.Try
import scala.util.matching.Regex

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

case class DocsInfo(domains: Map[String, String],
                    ips: Map[String, String],
                    ipMasks: Map[String, String],
                    jsonStrings: Map[String, (String, String)],
                    fieldLinks: Map[String, String],
                    fieldLinksSet: Set[String])

def decodeJsonMap(jsonString: String): JsonMap = {
  decode[JsonMap](jsonString) match {
    case Right(jsonMap) => jsonMap
    case Left(error) => throw new RuntimeException(error.getMessage)
  }
}

def filterJsonMap(jsonMap: JsonMap): JsonMap = {
  jsonMap.filter((key, v) => key.startsWith("output.") || key.startsWith("resource.") || key.startsWith("data.")).collect {
    case (key, value) =>
      val splitKey = key.split("\\.").toList
      val cleanedSplitKey = splitKey.map(k => k.replaceAll("\\[\\d+]", "")) // Remove indices
      val index = if key.startsWith("resource.") || key.startsWith("data.") then 2 else 1

      (("" +: cleanedSplitKey.slice(1, index) ++: cleanedSplitKey.drop(1 + index)).mkString("."), value)
  }
}

def definePatterns(): (Regex, Regex, Regex, Regex) = {
  val fieldPattern: Regex = """^(:?data\.)?yandex_[\w._-]+\.[\w._-]+\.[\w._-]+$""".r
  val domainPattern: Regex = """^(?:[a-zA-Z0-9](?:[a-zA-Z0-9\-]{0,61}[a-zA-Z0-9])?\.)+[a-zA-Z0-9][a-zA-Z0-9\-]{0,61}[a-zA-Z0-9]\.(ru|com|org|net)$""".r
  val ipPattern: Regex = """^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$""".r
  val ipMaskPattern: Regex = """^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)/([0-9]|[1-2][0-9]|3[0-2])$""".r
  (fieldPattern, domainPattern, ipPattern, ipMaskPattern)
}

// Helper function to get distinct values
def getDistinctValues(map: JsonMap): List[(String, String)] = {
  map.flatMap { case (key, valueList) =>
    valueList.collect {
      case JsonString(s) => (key, s)
    }
  }.toList.distinct
}

def getFieldValuePairs(distinctValues: List[(String, String)], fieldPattern: Regex): List[(String, String)] = {
  distinctValues.filter { case (_, value) =>
    value.startsWith("${") && value.endsWith("}") || fieldPattern.matches(value)
  }
}

def getModifiedFieldValues(fieldValues: List[(String, String)], fieldPattern: Regex): List[(String, String)] = {
  fieldValues.collect {
    case pair@(key, value) if value.startsWith("${") && value.endsWith("}") || fieldPattern.matches(value) =>
      (key, value.stripPrefix("${").stripSuffix("}"))
  }.collect {
    case (key, value) =>
      val splitValue = value.split("\\.").toList
      val cleanedSplitValue = splitValue.map(k => k.replaceAll("\\[\\d+]", "")) // Remove indices
      val index = if value.startsWith("data.") then 2 else 1
      (key, ("" +: cleanedSplitValue(index - 1) +: cleanedSplitValue.drop(1 + index)).mkString("."))
  }
}

def filterAndCollectValues(map: JsonMap, condition: String => Boolean): List[(String, String)] = {
  map.flatMap { case (key, valueList) =>
    valueList.collect {
      case JsonString(s) if condition(s) => (key, s)
    }
  }.toList
}

def getJsonValues(otherValues: JsonMap): List[(String, String, String)] = {
  otherValues.flatMap { case (key, valueList) =>
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
}

object DocsParser {
  def decodeAndFilterJson(jsonString: String): DocsInfo = {
    val jsonMap = decodeJsonMap(jsonString)
    val filteredJsonMap = filterJsonMap(jsonMap)

    val (fieldPattern, domainPattern, ipPattern, ipMaskPattern) = definePatterns()

    val distinctFlatPairs: List[(String, String)] = getDistinctValues(filteredJsonMap)

    val fieldValues: List[(String, String)] = getFieldValuePairs(distinctFlatPairs, fieldPattern)

    val otherValues: JsonMap = filteredJsonMap.filterNot { case (key, _) =>
      fieldValues.exists(_._1 == key)
    }

    val modifiedFieldValues: List[(String, String)] = getModifiedFieldValues(fieldValues, fieldPattern)

    val domainValues: List[(String, String)] = filterAndCollectValues(otherValues, domainPattern.matches)
    val ipValues: List[(String, String)] = filterAndCollectValues(otherValues, ipPattern.matches)
    val ipMaskValues: List[(String, String)] = filterAndCollectValues(otherValues, ipMaskPattern.matches)
    val jsonValues: List[(String, String, String)] = getJsonValues(otherValues)

    val domainValuesMap: Map[String, String] = domainValues.toMap
    val ipValuesMap: Map[String, String] = ipValues.toMap
    val ipMaskValuesMap: Map[String, String] = ipMaskValues.toMap
    val jsonValuesMap: Map[String, (String, String)] = jsonValues.map { case (key, value1, value2) => key -> (value1, value2) }.toMap
    val modifiedFieldValuesMap: Map[String, String] = modifiedFieldValues.toMap

    DocsInfo(
      domains = domainValuesMap,
      ips = ipValuesMap,
      ipMasks = ipMaskValuesMap,
      jsonStrings = jsonValuesMap,
      fieldLinks = modifiedFieldValuesMap,
      fieldLinksSet = modifiedFieldValuesMap.values.toSet
    )
  }
}