package patched.skuber.custom

import io.circe.{Codec, Decoder, Encoder}
import io.circe.generic.semiauto.{deriveCodec, deriveDecoder, deriveEncoder}
import skuber.api.client.EventType

object MyJsonProtocol {
  implicit val myEventCodec: Codec[MyEvent] = deriveCodec[MyEvent]

  implicit val myNodeCodec: Codec[MyNode] = deriveCodec[MyNode]

  implicit val allocatedResourcesCodec: Codec[AllocatedResources] = deriveCodec[AllocatedResources]

  implicit val containerStatesCodec: Codec[ContainerStates] = deriveCodec[ContainerStates]

  implicit val podConditionsCodec: Codec[PodConditions] = deriveCodec[PodConditions]

  implicit val myPodCodec: Codec[MyPod] = deriveCodec[MyPod]

  implicit val kuberInfoCodec: Codec[KuberInfo] = deriveCodec[KuberInfo]
  implicit val myWatchEventCodec: Codec[MyWatchEvent] = deriveCodec[MyWatchEvent]

  private val eventTypeEncoder: Encoder[EventType.EventType] = Encoder.encodeString.contramap(_.toString)
  private val eventTypeDecoder: Decoder[EventType.EventType] = Decoder.decodeString.map(EventType.withName)

  implicit val eventTypeCodec: Codec[EventType.EventType] =
    Codec.from(eventTypeDecoder, eventTypeEncoder)


}