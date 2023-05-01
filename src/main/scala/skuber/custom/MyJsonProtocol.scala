package skuber.custom

import skuber.api.client.EventType
import skuber.api.client.EventType.EventType
import skuber.custom.MyJsonProtocol.{jsonFormat16, jsonFormat5, jsonFormat8, jsonFormat9}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, RootJsonFormat}

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val myEventFormat: RootJsonFormat[MyEvent] = jsonFormat9(MyEvent.apply)
  implicit val myPodFormat: RootJsonFormat[MyPod] = jsonFormat16(MyPod.apply)
  implicit val myNodeFormat: RootJsonFormat[MyNode] = jsonFormat8(MyNode.apply)
  implicit val kuberInfoFormat: RootJsonFormat[KuberInfo] = jsonFormat5(KuberInfo.apply)
  implicit val myWatchEventFormat: RootJsonFormat[MyWatchEvent] = jsonFormat5(MyWatchEvent.apply)
  implicit val skuberEventTypeFormat: RootJsonFormat[EventType] = new RootJsonFormat[EventType] {
    override def write(obj: EventType): JsValue = JsString(obj.toString)

    override def read(json: JsValue): EventType = EventType.withName(json.convertTo[String])
  }
}