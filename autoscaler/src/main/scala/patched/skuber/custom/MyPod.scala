package patched.skuber.custom

import patched.skuber.operations.{formatDuration, stringToBoolean}
import skuber.{Event, Pod, objResourceToRef}
import skuber.Pod.Phase

import java.time.{Duration, ZonedDateTime}

case class AllocatedResources(cpuRequests: BigDecimal,
                              cpuLimits: BigDecimal,
                              memoryRequests: BigDecimal,
                              memoryLimits: BigDecimal,
                              ephemeralStorageRequests: BigDecimal,
                              ephemeralStorageLimits: BigDecimal,
                              hugepages2MiRequests: BigDecimal,
                              hugepages2MiLimits: BigDecimal,
                              hugepages1GiRequests: BigDecimal,
                              hugepages1GiLimits: BigDecimal)

case class ContainerStates(id: String)

case class PodConditions(conditionType: String, status: Boolean)

case class MyPod(ip: Option[String],
                 name: String,
                 status: Option[String],
                 startedAt: Option[String],
                 createdAt: Option[String],
                 age: Option[String],
                 ageInSec: Long,
                 restarts: Int,
                 states: List[ContainerStates],
                 allocatedResources: AllocatedResources,
                 namespace: String,
                 isSystem: Boolean,
                 failedScheduling: Boolean,
                 events: Map[String, MyEvent],
                 conditions: List[PodConditions],
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
      states = containerStatesList(pod.status.get.containerStatuses),
      allocatedResources = calculateAllocatedResources(resources),
      namespace = pod.namespace,
      isSystem = pod.namespace.equals("kube-system"),
      failedScheduling = newEvents.exists(_.reason.contains("FailedScheduling")) && pod.status.get.phase.contains(Phase.Pending) &&
        pod.status.get.conditions.exists(el => el.`_type` == "PodScheduled" && el.status == "False"),
      events = newEvents.map(el => el.uid -> el).toMap,
      uid = pod.metadata.uid,
      conditions = podConditionsList(pod.status.get.conditions)
    )
  }

  private def calculateAllocatedResources(resources: List[skuber.Resource.Requirements]): AllocatedResources = {
    val CPU = "cpu"
    val Memory = "memory"
    val EphemeralStorage = "ephemeral-storage"
    val HugePagesPrefix = "hugepages-" // hugepages-2Mi, hugepages-1Gi

    AllocatedResources(
      cpuRequests = resources.flatMap(_.requests.get(CPU)).map(_.amount).sum,
      cpuLimits = resources.flatMap(_.limits.get(CPU)).map(_.amount).sum,
      memoryRequests = resources.flatMap(_.requests.get(Memory)).map(_.amount).sum,
      memoryLimits = resources.flatMap(_.limits.get(Memory)).map(_.amount).sum,
      ephemeralStorageRequests = resources.flatMap(_.requests.get(EphemeralStorage)).map(_.amount).sum,
      ephemeralStorageLimits = resources.flatMap(_.limits.get(EphemeralStorage)).map(_.amount).sum,
      hugepages2MiRequests = resources.flatMap(_.requests.get(HugePagesPrefix + "2Mi")).map(_.amount).sum,
      hugepages2MiLimits = resources.flatMap(_.limits.get(HugePagesPrefix + "2Mi")).map(_.amount).sum,
      hugepages1GiRequests = resources.flatMap(_.requests.get(HugePagesPrefix + "1Gi")).map(_.amount).sum,
      hugepages1GiLimits = resources.flatMap(_.limits.get(HugePagesPrefix + "1Gi")).map(_.amount).sum,
    )
  }

  private def containerStatesList(containerStatuses: List[skuber.Container.Status]): List[ContainerStates] = {
    containerStatuses.map { containerStatus =>
      ContainerStates(id = containerStatus.state.get.id)
    }
  }

  private def podConditionsList(conditions: List[skuber.Pod.Condition]): List[PodConditions] = {
    conditions.map(el => PodConditions(conditionType = el.`_type`, status = stringToBoolean(el.status)))
  }

}