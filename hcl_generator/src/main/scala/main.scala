import io.circe.Decoder
import io.circe.jawn.decode

import scala.io.Source

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

  val source = Source.fromFile("./terraform-docs-extractor/results/yandex/3.json")
  val jsonString = source.getLines().mkString
  source.close()

  // Decode the JSON string into Map[String, List[String]]
  val map = decode[JsonMap](jsonString) match {
    case Right(jsonMap) => jsonMap
    case Left(error) => throw new RuntimeException(error.getMessage)
  }

  // The rest of your code goes here, now using decodedJson as your Map[String, List[String]]

  // Define the pattern
  val pattern: Regex = """^(:?data\.)?yandex_[\w._-]+\.[\w._-]+\.[\w._-]+$""".r

  // Flatten the map values, get distinct values
  val distinctFlatValues: List[String] = map.values.flatten.collect {
    case JsonString(s) => s
  }.toList.distinct

  // Filter out values that are field links
  val filteredValues: List[String] = distinctFlatValues.filter { value =>
    value.startsWith("${") && value.endsWith("}") || pattern.matches(value)
  }

  // Print the values
  filteredValues.foreach(println)
}