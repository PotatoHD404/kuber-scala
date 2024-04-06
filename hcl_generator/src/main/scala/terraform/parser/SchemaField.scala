package terraform.parser

import io.circe.{Codec, Decoder, Encoder, HCursor}
import io.circe.generic.semiauto.{deriveCodec, deriveDecoder, deriveEncoder}

case class SchemaField(
                        AtLeastOneOf: Option[List[String]],
                        Computed: Boolean,
                        ComputedWhen: List[String],
                        ConfigMode: Option[Int],
                        ConflictsWith: List[String],
                        Deprecated: String,
                        Description: String,
                        DiffSuppressOnRefresh: Option[Boolean],
                        Elem: Option[Either[TerraformResource, SchemaField]],
                        ExactlyOneOf: Option[List[String]],
                        ForceNew: Boolean,
                        InputDefault: String,
                        MaxItems: Int,
                        MinItems: Int,
                        Optional: Boolean,
                        Required: Boolean,
                        RequiredWith: Option[List[String]],
                        Sensitive: Boolean,
                        Type: Int,
                      )

object SchemaField {
  // Add these implicit decoders for the `Either` type in the `Elem` field
  implicit val decodeResourceOrSchemaField: Decoder[Either[TerraformResource, SchemaField]] =
    Decoder[TerraformResource].map(Left(_)).or(Decoder[SchemaField].map(Right(_)))

  implicit val encodeResourceOrSchemaField: Encoder[Either[TerraformResource, SchemaField]] =
    Encoder.instance {
      case Left(resource) => Encoder[TerraformResource].apply(resource)
      case Right(schemaField) => Encoder[SchemaField].apply(schemaField)
    }

  implicit val decodeSchemaField: Decoder[SchemaField] = deriveDecoder

  implicit val encodeSchemaField: Encoder[SchemaField] = deriveEncoder
}