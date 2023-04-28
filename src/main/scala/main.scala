import skuber.*
import skuber.json.format.*
import akka.actor.ActorSystem
import skuber.apps.DeploymentList

import scala.concurrent.duration.*
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import spray.json.*
import DefaultJsonProtocol.*

import java.time.Duration
import java.time.ZonedDateTime
import java.util.Optional
import scala.util.Try


def formatDuration(duration: Duration): String = {
  val days = duration.toDaysPart
  val hours = duration.toHoursPart
  val minutes = duration.toMinutesPart
  val seconds = duration.toSecondsPart

  s"${if (days > 0) days + "d" else ""}${if (hours > 0) hours + "h" else ""}${if (minutes > 0) minutes + "m" else ""}${if (seconds > 0) seconds + "s" else ""}".trim
}

def toOption(value: String): Option[String] = {
  if (value.isEmpty) None
  else Some(value)
}

def stringToFloat(value: String): Option[Float] = {
  if (value.endsWith("m")) {
    Try((value.dropRight(1).toFloat) / 1000).toOption
  } else {
    Try(value.toFloat).toOption
  }
}

case class MyPod(ip: Option[String], name: String, status: Option[String], startedAt: Option[String], age: Option[String], restarts: Int, states: List[String], allocatedResources: Map[String, List[BigDecimal]], namespace: String)

object MyPod {
  def fromPod(pod: Pod): MyPod = {
    val resources = pod.spec.get.containers.flatMap(_.resources)

    val CPU = "cpu"
    val Memory = "memory"
    val EphemeralStorage = "ephemeral-storage"
    val HugePagesPrefix = "hugepages-" // hugepages-2Mi, hugepages-1Gi
    MyPod(
      ip = pod.status.get.podIP,
      name = pod.name,
      status = pod.status.get.phase.map(_.toString),
      startedAt = pod.status.get.startTime.map(_.toString),
      age = pod.status.get.startTime.map { startTime =>
        val now = ZonedDateTime.now()
        val age = Duration.between(startTime, now)
        formatDuration(age)
      },
      restarts = pod.status.get.containerStatuses.map(_.restartCount).sum,
      states = pod.status.get.containerStatuses.map { containerStatus =>
        containerStatus.state.get.id
      },
      allocatedResources  = Map(
        "cpu-requests" -> resources.flatMap(_.requests.get(CPU)).map(_.amount),
        "cpu-limits" -> resources.flatMap(_.limits.get(CPU)).map(_.amount),
        "memory-requests" -> resources.flatMap(_.requests.get(Memory)).map(_.amount),
        "memory-limits" -> resources.flatMap(_.limits.get(Memory)).map(_.amount),
        "ephemeral-storage-requests" -> resources.flatMap(_.requests.get(EphemeralStorage)).map(_.amount),
        "ephemeral-storage-limits" -> resources.flatMap(_.limits.get(EphemeralStorage)).map(_.amount),
        "hugepages-2Mi-requests" -> resources.flatMap(_.requests.get(HugePagesPrefix + "2Mi")).map(_.amount),
        "hugepages-2Mi-limits" -> resources.flatMap(_.limits.get(HugePagesPrefix + "2Mi")).map(_.amount),
        "hugepages-1Gi-requests" -> resources.flatMap(_.requests.get(HugePagesPrefix + "1Gi")).map(_.amount),
        "hugepages-1Gi-limits" -> resources.flatMap(_.limits.get(HugePagesPrefix + "1Gi")).map(_.amount),
      ),
      namespace = pod.namespace
    )
  }
}

case class MyNode(name: String, status: Map[String, Boolean], pods: List[MyPod], ip: Map[String, String], allocatedResources: Map[String, BigDecimal], capacity: Map[String, BigDecimal], allocatable: Map[String, BigDecimal])

object MyNode {
  def fromNode(node: Node, pods: List[Pod]): MyNode = {


    val newPods = pods.map(MyPod.fromPod)
    val _ = node.spec.get.unschedulable

    MyNode(
      name = node.name,
      status = node.status.get.conditions.map { condition => {
        condition.`_type` -> {
          condition.status match {
            case "True" => true
            case "False" => false
            case _ => throw new Exception("Unknown status")
          }
        }
      }

      }.toMap,
      pods = newPods.filterNot(_.namespace.equals("kube-system")),
      ip = node.status.get.addresses.map(a => a.`_type` -> a.address).toMap,
      allocatedResources = newPods.flatMap(_.allocatedResources).groupBy(_._1).map { case (k, v) => k -> v.flatMap(_._2).sum },
//      totalCpuRequests = newPods.flatMap(_.allocatedResources.get("cpu-requests")).sum,
//      totalCpuLimits = newPods.flatMap(_.cpuLimits).sum,
//      totalMemoryRequests = newPods.flatMap(_.memoryRequests).sum,
//      totalMemoryLimits = newPods.flatMap(_.memoryLimits).sum,
      capacity = node.status.get.capacity.map((k, v) => k -> v.amount),
      allocatable = node.status.get.allocatable.map((k, v) => k -> v.amount),
    )
    //      ip = node.status.get.capacity,
  }
}

case class KuberInfo (nodes: Map[String, MyNode], podsWithoutNode: List[MyPod])

object KuberInfo {
  def fromNodesAndPods(nodes: List[Node], pods: List[Pod]): KuberInfo = {
    val myNodes = nodes.map { node =>
      val nodePods = pods.filter(_.spec.get.nodeName.equals(node.name))
      MyNode.fromNode(node, nodePods)
    }.map(n => n.name -> n).toMap
    val podsWithoutNode = pods.filter(_.spec.get.nodeName.isEmpty).filterNot(_.namespace.equals("kube-system")).map(MyPod.fromPod)
    KuberInfo(myNodes, podsWithoutNode)
  }
}

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val myPodFormat: RootJsonFormat[MyPod] = jsonFormat9(MyPod.apply)
  implicit val myNodeFormat: RootJsonFormat[MyNode] = jsonFormat7(MyNode.apply)
  implicit val kuberInfoFormat: RootJsonFormat[KuberInfo] = jsonFormat2(KuberInfo.apply)
}

// TODO: once it spontaneously crashed so need to handle exceptions properly

@main
def main(): Unit = {
  import MyJsonProtocol._

  implicit val system: ActorSystem = ActorSystem()
  implicit val dispatcher: ExecutionContextExecutor = system.dispatcher

  val k8s = k8sInit

  try {
    // List nodes

    val nodes = Await.result(k8s.list[NodeList](), 10.seconds)
    println("Nodes:")
    println()

    // List pods
    val namespaces = Await.result(k8s.list[NamespaceList](), 10.seconds)
    val pods = Await.result(Future.sequence(namespaces.items.map(el => k8s.list[PodList](Some(el.name)))), 10.seconds).flatten
    val info = KuberInfo.fromNodesAndPods(nodes, pods)
//    pods.filter(_.spec.get.nodeName.isEmpty).filterNot(_.namespace.equals("kube-system")).map(_.spec.get.containers.flatMap(_.resources)).foreach(println)

    info.toJson.prettyPrint.foreach(println)


  } catch {
    case e: Exception => throw e
  } finally {
    system.terminate()
  }
}