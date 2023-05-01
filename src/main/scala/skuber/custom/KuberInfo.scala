package skuber.custom

import skuber.objResourceToRef
import skuber.{Event, NamespaceList, Node, Pod}

case class KuberInfo(nodes: Map[String, MyNode],
                     podsWithoutNode: Map[String, MyPod],
                     unscheduledPods: Map[String, MyPod],
                     events: List[MyEvent],
                     namespaces: Set[String])

object KuberInfo {
  def fromNodesAndPods(nodes: List[Node], pods: List[Pod], events: List[Event], namespaces: NamespaceList): KuberInfo = {
    val myNodes = nodes.map { node =>
      val nodePods = pods.filter(_.spec.get.nodeName.equals(node.name))
      MyNode.fromNode(node, nodePods, events)
    }.map(n => n.name -> n).toMap
    val podsWithoutNode = pods.filter(_.spec.get.nodeName.isEmpty).filterNot(_.namespace.equals("kube-system")).map(MyPod.fromPod(_, events))
    val unscheduledPods = pods.map(MyPod.fromPod(_, events)).filter(_.failedScheduling)
    KuberInfo(nodes = myNodes,
      podsWithoutNode = podsWithoutNode.map(pod => pod.uid -> pod).toMap,
      unscheduledPods = unscheduledPods.map(pod => pod.uid -> pod).toMap,
      events = events.map(MyEvent.fromEvent),
      namespaces = namespaces.items.map(_.metadata.name).toSet
    )
  }

  def createEmpty: KuberInfo = KuberInfo(Map.empty, Map.empty, Map.empty, List.empty, Set.empty)
}