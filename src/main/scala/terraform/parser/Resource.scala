package terraform.parser

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
case class Resource(
                       DeprecationMessage: String,
                       Description: String,
                       Schema: Map[String, SchemaField]
                     )

object Resource {
  implicit val codec: Codec[Resource] = deriveCodec
}