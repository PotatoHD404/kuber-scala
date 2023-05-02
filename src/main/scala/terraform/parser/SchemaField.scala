package terraform.parser

import io.circe.{Codec, Decoder, Encoder, HCursor}
import io.circe.generic.semiauto.{deriveCodec, deriveDecoder, deriveEncoder}

case class SchemaField(
                        AtLeastOneOf: List[String],
                        Computed: Boolean,
                        ComputedWhen: List[String],
                        ConfigMode: Int,
                        ConflictsWith: List[String],
                        Deprecated: String,
                        Description: String,
                        DiffSuppressOnRefresh: Boolean,
                        Elem: Option[Either[Resource, SchemaField]],
                        ExactlyOneOf: List[String],
                        ForceNew: Boolean,
                        InputDefault: String,
                        MaxItems: Int,
                        MinItems: Int,
                        Optional: Boolean,
                        Required: Boolean,
                        RequiredWith: List[String],
                        Sensitive: Boolean,
                        Type: Int,
                      )

object SchemaField {
  // Add these implicit decoders for the `Either` type in the `Elem` field
  implicit val decodeResourceOrSchemaField: Decoder[Either[Resource, SchemaField]] =
    Decoder[Resource].map(Left(_)).or(Decoder[SchemaField].map(Right(_)))

  implicit val encodeResourceOrSchemaField: Encoder[Either[Resource, SchemaField]] =
    Encoder.instance {
      case Left(resource) => Encoder[Resource].apply(resource)
      case Right(schemaField) => Encoder[SchemaField].apply(schemaField)
    }

  implicit val decodeSchemaField: Decoder[SchemaField] = deriveDecoder

  implicit val encodeSchemaField: Encoder[SchemaField] = deriveEncoder
}