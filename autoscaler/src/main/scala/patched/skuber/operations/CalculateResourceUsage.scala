package patched.skuber.operations

import cats.effect.IO
import patched.skuber.custom.KuberInfo
import play.api.libs.json.Format
import skuber.api.client.KubernetesClient
import skuber.{EventList, NamespaceList, Node, NodeList, PodList}
import terraform.kubenetes_clusters.Cluster
import skuber.json.format.*

implicit val nodeListFormat: Format[NodeList] = ListResourceFormat[Node]
import patched.skuber.operations.Conversions.toIO
import cats.syntax.all.toTraverseOps
import skuber.toList

case class NodeResourceUsage(nodeName: String, cpuUsage: Double, memoryUsage: Double)

def calculateNodeResourceUsage(kuberInfo: KuberInfo): List[NodeResourceUsage] = {
  kuberInfo.nodes.map { (name, node) =>
    val nodePods = node.pods
    val cpuUsage = nodePods.values.map(_.allocatedResources.cpuRequests).sum
    val memoryUsage = nodePods.values.map(_.allocatedResources.memoryRequests).sum

    NodeResourceUsage(name, cpuUsage.doubleValue, memoryUsage.doubleValue)
  }.toList
}

def checkResourceUsageAndScale()(implicit k8s: KubernetesClient, cluster: Cluster): IO[Unit] = {
  for {
    nodes <- k8s.list[NodeList]().toIO
    namespaces <- k8s.list[NamespaceList]().toIO
    pods <- namespaces.items.traverse(ns => k8s.list[PodList](Some(ns.name)).toIO).map(_.flatten)
    events <- k8s.list[EventList]().toIO
    kuberInfo = KuberInfo.fromNodesAndPods(nodes, pods, events, namespaces)

    nodeResourceUsage = calculateNodeResourceUsage(kuberInfo)

    totalCpuCapacity = kuberInfo.nodes.values.flatMap(_.capacity.get("cpu")).sum.doubleValue
    totalMemoryCapacity = kuberInfo.nodes.values.flatMap(_.capacity.get("memory")).sum.doubleValue

    totalCpuUsage = nodeResourceUsage.map(_.cpuUsage).sum
    totalMemoryUsage = nodeResourceUsage.map(_.memoryUsage).sum

    cpuUtilization = totalCpuUsage / totalCpuCapacity
    memoryUtilization = totalMemoryUsage / totalMemoryCapacity

    _ = if (cpuUtilization > 0.9 || memoryUtilization > 0.9) {
      val cpuNeeded = math.ceil((totalCpuUsage / 0.9) - totalCpuCapacity).toInt
      val memoryNeeded = math.ceil((totalMemoryUsage / 0.9) - totalMemoryCapacity).toInt
      val nodesNeeded = math.max(cpuNeeded / 2, memoryNeeded / 4)
      require(nodesNeeded < 5, "Safety check failed, nodes >= 5")
      cluster.upscale(nodesNeeded)
      cluster.applyTerraformConfig()
    } else if (cpuUtilization < 0.5 && memoryUtilization < 0.5) {
      val cpuExcess = math.floor(totalCpuCapacity - (totalCpuUsage / 0.5)).toInt
      val memoryExcess = math.floor(totalMemoryCapacity - (totalMemoryUsage / 0.5)).toInt
      val nodesExcess = math.min(cpuExcess / 2, memoryExcess / 4)
      require(nodesExcess < 5, "Safety check failed, nodes >= 5")
      cluster.downscale(nodesExcess)
      cluster.applyTerraformConfig()
    }
  } yield ()
}