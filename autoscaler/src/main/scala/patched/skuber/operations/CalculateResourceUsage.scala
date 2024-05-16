package patched.skuber.operations

import cats.effect.IO
import skuber.api.client.{Cluster, KubernetesClient}
import skuber.{NodeList, PodList}


case class NodeResourceUsage(nodeName: String, cpuUsage: Double, memoryUsage: Double)

def calculateNodeResourceUsage(nodes: NodeList, pods: PodList): List[NodeResourceUsage] = {
  nodes.items.map { node =>
    val nodePods = pods.items.filter(_.spec.exists(_.nodeName.contains(node.name)))
    val cpuUsage = nodePods.map(_.spec.flatMap(_.containers.map(_.resources.flatMap(_.requests.get("cpu")).map(_.amount).getOrElse(0.0))).sum).sum
    val memoryUsage = nodePods.map(_.spec.flatMap(_.containers.map(_.resources.flatMap(_.requests.get("memory")).map(_.amount).getOrElse(0.0))).sum).sum

    NodeResourceUsage(node.name, cpuUsage, memoryUsage)
  }
}


def checkResourceUsageAndScale()(implicit k8s: KubernetesClient, cluster: Cluster): IO[Unit] = {
  for {
    nodes <- k8s.list[NodeList]().toIO
    pods <- k8s.list[PodList]().toIO
    nodeResourceUsage = calculateNodeResourceUsage(nodes, pods)

    totalCpuCapacity = nodes.items.map(_.status.flatMap(_.capacity.get("cpu")).map(_.amount).getOrElse(0.0)).sum
    totalMemoryCapacity = nodes.items.map(_.status.flatMap(_.capacity.get("memory")).map(_.amount).getOrElse(0.0)).sum

    totalCpuUsage = nodeResourceUsage.map(_.cpuUsage).sum
    totalMemoryUsage = nodeResourceUsage.map(_.memoryUsage).sum

    cpuUtilization = totalCpuUsage / totalCpuCapacity
    memoryUtilization = totalMemoryUsage / totalMemoryCapacity

    _ = if (cpuUtilization > 0.9 || memoryUtilization > 0.9) {
      val cpuNeeded = math.ceil((totalCpuUsage / 0.9) - totalCpuCapacity).toInt
      val memoryNeeded = math.ceil((totalMemoryUsage / 0.9) - totalMemoryCapacity).toInt
      val nodesNeeded = math.max(cpuNeeded / 2, memoryNeeded / 4)
      cluster.upscale(nodesNeeded)
    } else if (cpuUtilization < 0.5 && memoryUtilization < 0.5) {
      val cpuExcess = math.floor(totalCpuCapacity - (totalCpuUsage / 0.5)).toInt
      val memoryExcess = math.floor(totalMemoryCapacity - (totalMemoryUsage / 0.5)).toInt
      val nodesExcess = math.min(cpuExcess / 2, memoryExcess / 4)
      cluster.downscale(nodesExcess)
    }
  } yield ()
}