package terraform.parser

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class SchemaField(
                        AtLeastOneOf: List[String],
                        Computed: Boolean,
                        ComputedWhen: List[String],
                        ConfigMode: Int,
                        ConflictsWith: List[String],
                        Deprecated: String,
                        Description: String,
                        DiffSuppressOnRefresh: Boolean,
                        ExactlyOneOf: List[String],
                        ForceNew: Boolean,
                        InputDefault: String,
                        MaxItems: Int,
                        MinItems: Int,
                        Optional: Boolean,
                        Required: Boolean,
                        RequiredWith: List[String],
                        Sensitive: Boolean,
                        Type: Int
                      )

object SchemaField {
  implicit val codec: Codec[SchemaField] = deriveCodec
}
