package terraform

import scala.util.matching.Regex

case class Region(value: String)

case class CIDRBlock(value: String) {
  private val cidrPattern: Regex = """^(\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}\/\d{1,2})$""".r
  require(cidrPattern.matches(value), s"Invalid CIDR block: $value")
}
case class PortRange(from: Int, to: Int) {
  require(from >= 0 && from <= 65535, s"Invalid from port: $from")
  require(to >= 0 && to <= 65535, s"Invalid to port: $to")
  require(from <= to, s"From port ($from) must be less than or equal to the to port ($to)")
}
case class Image(publisher: String, offer: String, sku: String)