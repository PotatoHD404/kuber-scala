package skuber.custom

import skuber.{Event, Pod, formatDuration, objResourceToRef, stringToBoolean}
import skuber.Pod.Phase

import java.time.{Duration, ZonedDateTime}
case class MyPod(ip: Option[String],
                 name: String, status: Option[String],
                 startedAt: Option[String],
                 createdAt: Option[String],
                 age: Option[String],
                 ageInSec: Long,
                 restarts: Int,
                 states: List[String],
                 allocatedResources: Map[String, BigDecimal],
                 namespace: String,
                 isSystem: Boolean,
                 failedScheduling: Boolean,
                 events: Map[String, MyEvent],
                 conditions: Map[String, Boolean],
                 uid: String) {

  override def equals(obj: Any): Boolean = obj match {
    case other: MyPod =>
      this.ip == other.ip &&
        this.name == other.name &&
        this.status == other.status &&
        this.startedAt == other.startedAt &&
        this.createdAt == other.createdAt &&
        this.restarts == other.restarts &&
        this.states == other.states &&
        this.allocatedResources == other.allocatedResources &&
        this.namespace == other.namespace &&
        this.isSystem == other.isSystem &&
        this.failedScheduling == other.failedScheduling &&
        this.events == other.events &&
        this.conditions == other.conditions &&
        this.uid == other.uid
    case _ => false
  }

  // You may also want to override hashCode to be consistent with the new equals implementation
  override def hashCode(): Int = {
    val fields = Seq(ip, name, status, startedAt, createdAt, ageInSec, restarts, states, allocatedResources, namespace, isSystem, failedScheduling, events, conditions, uid)
    fields.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

object MyPod {
  def fromPod(pod: Pod): MyPod = fromPod(pod, List.empty)

  def fromPod(pod: Pod, events: List[Event]): MyPod = {
    val resources = pod.spec.get.containers.flatMap(_.resources)

    val CPU = "cpu"
    val Memory = "memory"
    val EphemeralStorage = "ephemeral-storage"
    val HugePagesPrefix = "hugepages-" // hugepages-2Mi, hugepages-1Gi
    var duration: Duration = Duration.ZERO
    val newEvents: List[MyEvent] = events.filter(_.involvedObject.uid.contains(pod.metadata.uid)).map(MyEvent.fromEvent)

    MyPod(
      ip = pod.status.get.podIP,
      name = pod.name,
      status = pod.status.get.phase.map(_.toString),
      createdAt = pod.metadata.creationTimestamp.map(_.toString),
      startedAt = pod.status.get.startTime.map(_.toString),
      age = pod.metadata.creationTimestamp.map { createdAt =>
        val now = ZonedDateTime.now()
        duration = Duration.between(createdAt, now)
        formatDuration(duration)
      },
      ageInSec = duration.toSeconds,
      restarts = pod.status.get.containerStatuses.map(_.restartCount).sum,
      states = pod.status.get.containerStatuses.map { containerStatus =>
        containerStatus.state.get.id
      },
      allocatedResources = Map(
        "cpu-requests" -> resources.flatMap(_.requests.get(CPU)).map(_.amount).sum,
        "cpu-limits" -> resources.flatMap(_.limits.get(CPU)).map(_.amount).sum,
        "memory-requests" -> resources.flatMap(_.requests.get(Memory)).map(_.amount).sum,
        "memory-limits" -> resources.flatMap(_.limits.get(Memory)).map(_.amount).sum,
        "ephemeral-storage-requests" -> resources.flatMap(_.requests.get(EphemeralStorage)).map(_.amount).sum,
        "ephemeral-storage-limits" -> resources.flatMap(_.limits.get(EphemeralStorage)).map(_.amount).sum,
        "hugepages-2Mi-requests" -> resources.flatMap(_.requests.get(HugePagesPrefix + "2Mi")).map(_.amount).sum,
        "hugepages-2Mi-limits" -> resources.flatMap(_.limits.get(HugePagesPrefix + "2Mi")).map(_.amount).sum,
        "hugepages-1Gi-requests" -> resources.flatMap(_.requests.get(HugePagesPrefix + "1Gi")).map(_.amount).sum,
        "hugepages-1Gi-limits" -> resources.flatMap(_.limits.get(HugePagesPrefix + "1Gi")).map(_.amount).sum,
      ),
      namespace = pod.namespace,
      isSystem = pod.namespace.equals("kube-system"),
      // get events list and check if there is a failed scheduling event
      failedScheduling = newEvents.exists(_.reason.contains("FailedScheduling")) && pod.status.get.phase.contains(Phase.Pending) &&
        pod.status.get.conditions.exists(el => el.`_type` == "PodScheduled" && el.status == "False"),
      events = newEvents.map(el => el.uid -> el).toMap,
      uid = pod.metadata.uid,
      conditions = pod.status.get.conditions.map(el => el.`_type` -> stringToBoolean(el.status)).toMap
    )
  }
}