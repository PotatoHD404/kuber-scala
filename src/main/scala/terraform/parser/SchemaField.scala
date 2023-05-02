package terraform.parser

import io.circe.{Codec, Decoder, Encoder, HCursor}
import io.circe.generic.semiauto.{deriveCodec, deriveEncoder}

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

  implicit val decodeSchemaField: Decoder[SchemaField] = (c: HCursor) => {
    for {
      atLeastOneOf         <- c.downField("AtLeastOneOf").as[Option[List[String]]].map(_.getOrElse(List.empty))
      computed             <- c.downField("Computed").as[Boolean]
      computedWhen         <- c.downField("ComputedWhen").as[Option[List[String]]].map(_.getOrElse(List.empty))
      configMode           <- c.downField("ConfigMode").as[Int]
      conflictsWith        <- c.downField("ConflictsWith").as[Option[List[String]]].map(_.getOrElse(List.empty))
      deprecated           <- c.downField("Deprecated").as[String]
      description          <- c.downField("Description").as[String]
      diffSuppressOnRefresh <- c.downField("DiffSuppressOnRefresh").as[Boolean]
      elem                 <- c.downField("Elem").as[Option[Either[Resource, SchemaField]]]
      exactlyOneOf         <- c.downField("ExactlyOneOf").as[Option[List[String]]].map(_.getOrElse(List.empty))
      forceNew             <- c.downField("ForceNew").as[Boolean]
      inputDefault         <- c.downField("InputDefault").as[String]
      maxItems             <- c.downField("MaxItems").as[Int]
      minItems             <- c.downField("MinItems").as[Int]
      optional             <- c.downField("Optional").as[Boolean]
      required             <- c.downField("Required").as[Boolean]
      requiredWith         <- c.downField("RequiredWith").as[Option[List[String]]].map(_.getOrElse(List.empty))
      sensitive            <- c.downField("Sensitive").as[Boolean]
      fieldType            <- c.downField("Type").as[Int]
    } yield SchemaField(
      atLeastOneOf,
      computed,
      computedWhen,
      configMode,
      conflictsWith,
      deprecated,
      description,
      diffSuppressOnRefresh,
      elem,
      exactlyOneOf,
      forceNew,
      inputDefault,
      maxItems,
      minItems,
      optional,
      required,
      requiredWith,
      sensitive,
      fieldType
    )
  }

  implicit val encodeSchemaField: Encoder[SchemaField] = deriveEncoder
}