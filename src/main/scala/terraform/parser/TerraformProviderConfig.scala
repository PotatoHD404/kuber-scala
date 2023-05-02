package terraform.parser

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class TerraformProviderConfig(
                                    DataSourcesMap: Map[String, Resource],
                                    ResourcesMap: Map[String, Resource],
                                    Schema: Map[String, SchemaField])

object TerraformProviderConfig {
  implicit val codec: Codec[TerraformProviderConfig] = deriveCodec
}