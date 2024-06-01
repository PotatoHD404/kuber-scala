package patched.skuber.operations

import cats.effect.IO
import patched.skuber.custom.KuberInfo
import play.api.libs.json.Format
import skuber.api.client.KubernetesClient
import skuber.{EventList, NamespaceList, Node, NodeList, PodList}
import skuber.json.format.*
import terraform.kubenetes.clusters.{Cluster, Instance}
import cats.effect.unsafe.implicits.global

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

def deleteRemovedInstances(removedInstances: List[Instance])(implicit k8s: KubernetesClient): Unit = {
  removedInstances.foreach { instance =>
    val nodeNameOption = getNodeNameByIp(instance.internalIp)
    nodeNameOption.foreach { nodeName =>
      k8s.delete[Node](nodeName).toIO.attempt.unsafeRunSync() match {
        case Right(_) => println(s"Node $nodeName deleted successfully")
        case Left(ex) => println(s"Error deleting node $nodeName: ${ex.getMessage}")
      }
    }
  }
}

def getNodeNameByIp(ip: String)(implicit k8s: KubernetesClient): Option[String] = {
  val nodes = k8s.list[NodeList]().toIO.unsafeRunSync().items
  nodes.find(node => node.status.exists(_.addresses.exists(_.address == ip))).map(_.name)
}

def checkResourceUsageAndScale()(implicit k8s: KubernetesClient, cluster: Cluster): IO[Unit] = {
  for {
    defaultNodes <- k8s.list[NodeList]().toIO
    systemNodes <- k8s.list[NodeList](Some("kube-system")).toIO
    nodes = defaultNodes ++ systemNodes
    _ = cluster.readTerraformState()
    namespaces <- k8s.list[NamespaceList]().toIO
    pods <- namespaces.items.traverse(ns => k8s.list[PodList](Some(ns.name)).toIO).map(_.flatten)
    events <- k8s.list[EventList]().toIO
    kuberInfo = KuberInfo.fromNodesAndPods(nodes, pods, events, namespaces)
    nodeCount = cluster.getInstancesCount
    nodeResourceUsage = calculateNodeResourceUsage(kuberInfo)

    totalCpuCapacity = kuberInfo.nodes.values.flatMap(_.capacity.get("cpu")).sum.doubleValue.round
    totalMemoryCapacity = kuberInfo.nodes.values.flatMap(_.capacity.get("memory")).sum.doubleValue.round

    totalCpuUsage = nodeResourceUsage.map(_.cpuUsage).sum
    totalMemoryUsage = nodeResourceUsage.map(_.memoryUsage).sum

    cpuUtilization = totalCpuUsage / totalCpuCapacity
    memoryUtilization = totalMemoryUsage / totalMemoryCapacity

    _ = println(s"CPU Utilization: ${cpuUtilization * 100}%")
    _ = println(s"Memory Utilization: ${memoryUtilization * 100}%")

    _ = if (cpuUtilization >= 0.5 || memoryUtilization >= 0.5) {
      require(1 + nodeCount < 5, "Safety check failed, nodes >= 5")
      if (1 > 0) {
        println(s"Upscaling by 1 node")
        cluster.upscale(1)
      } else {
        println("No scaling action required")
      }
    } else if (cpuUtilization < 0.1 && memoryUtilization < 0.1 && nodeCount > 1) {
        println(s"Downscaling by 1 node")
        val removedInstances = cluster.downscale(1)
        deleteRemovedInstances(removedInstances)
    } else {
      println("No scaling action required")
    }
  } yield ()
}