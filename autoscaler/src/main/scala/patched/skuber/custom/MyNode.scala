package patched.skuber.custom

import skuber.{Event, Node, NodeList, Pod}
import patched.skuber.operations.stringToBoolean
import play.api.libs.json.Format

case class MyNode(name: String,
                  status: Map[String, Boolean],
                  pods: Map[String, MyPod],
                  ip: Map[String, String],
                  allocatedResources: AllocatedResources,
                  capacity: Map[String, BigDecimal],
                  allocatable: Map[String, BigDecimal],
                  uid: String)

object MyNode {

  def fromNode(node: Node): MyNode = fromNode(node, List.empty, List.empty)

  def fromNode(node: Node, pods: List[Pod], events: List[Event]): MyNode = {
    val newPods = pods.map(MyPod.fromPod(_, events))

    val resources = newPods.map(_.allocatedResources)
    val allocatedResources = AllocatedResources(
      cpuRequests = resources.map(_.cpuRequests).sum,
      cpuLimits = resources.map(_.cpuLimits).sum,
      memoryRequests = resources.map(_.memoryRequests).sum,
      memoryLimits = resources.map(_.memoryLimits).sum,
      ephemeralStorageRequests = resources.map(_.ephemeralStorageRequests).sum,
      ephemeralStorageLimits = resources.map(_.ephemeralStorageLimits).sum,
      hugepages2MiRequests = resources.map(_.hugepages2MiRequests).sum,
      hugepages2MiLimits = resources.map(_.hugepages2MiLimits).sum,
      hugepages1GiRequests = resources.map(_.hugepages1GiRequests).sum,
      hugepages1GiLimits = resources.map(_.hugepages1GiLimits).sum
    )

    MyNode(
      name = node.name,
      status = node.status.get.conditions.map(condition => condition.`_type` -> stringToBoolean(condition.status)).toMap,
      pods = newPods.filterNot(_.isSystem).map(pod => pod.uid -> pod).toMap,
      ip = node.status.get.addresses.map(a => a.`_type` -> a.address).toMap,
      allocatedResources = allocatedResources,
      capacity = node.status.get.capacity.map((k, v) => k -> v.amount),
      allocatable = node.status.get.allocatable.map((k, v) => k -> v.amount),
      uid = node.metadata.uid,
    )
  }
}