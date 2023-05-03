package terraform.parser

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
case class TerraformResource(
                       DeprecationMessage: String,
                       Description: String,
                       Schema: Map[String, SchemaField]
                     )

object TerraformResource {
  implicit val codec: Codec[TerraformResource] = deriveCodec
}