package terraform.parser

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
case class DataSource(
                       DeprecationMessage: String,
                       Description: String,
                       Schema: Map[String, SchemaField]
                     )

object DataSource {
  implicit val codec: Codec[DataSource] = deriveCodec
}