package patched.skuber.custom

import skuber.Event

case class MyEvent(
                    reason: Option[String],
                    message: Option[String],
                    source: Option[String],
                    firstSeen: Option[String],
                    lastSeen: Option[String],
                    count: Int,
                    eventType: Option[String],
                    uid: String,
                    podUid: Option[String] = None,
                  )

object MyEvent {
  def fromEvent(event: Event): MyEvent = {

    MyEvent(
      reason = event.reason,
      message = event.message,
      source = event.source.get.component,
      firstSeen = event.firstTimestamp.map(_.toString),
      lastSeen = event.lastTimestamp.map(_.toString),
      count = event.metadata.generation,
      eventType = event.`type`,
      uid = event.involvedObject.uid,
      podUid = {
        if (event.involvedObject.kind == "Pod") Some(event.involvedObject.uid)
        else None
      }
    )
  }
}