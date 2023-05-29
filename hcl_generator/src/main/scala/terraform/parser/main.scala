package terraform.parser

import terraform.parser.DocsParser

import scala.io.Source

@main
def main(): Unit = {
  // Assuming you have the JSON string
  val source = Source.fromFile("./terraform-docs-extractor/results/yandex.json")
  val jsonString = source.getLines().mkString
  source.close()

  val filteredJson = DocsParser.decodeAndFilterJson(jsonString)

  println(s"Domains:\n\n${filteredJson.domains.map((key, value) => s"$key -> $value").mkString("\n")}")
  println(s"IPs:\n\n${filteredJson.ips.map((key, value) => s"$key -> $value").mkString("\n")}")
  println(s"IP Masks:\n\n${filteredJson.ipMasks.map((key, value) => s"$key -> $value").mkString("\n")}")
  println(s"JSON Strings:\n\n${filteredJson.jsonStrings.map((key, value, t) => s"$key -> $value: $t").mkString("\n")}")
  println(s"Field Links:\n\n${filteredJson.fieldLinks.map((key, value) => s"$key -> $value").mkString("\n")}")
}