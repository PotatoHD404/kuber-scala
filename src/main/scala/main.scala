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
// from format to duration
def toDuration(value: String): Option[Duration] = {
  if (value.isEmpty) None
  else {
    val days = value.split("d").headOption.map(_.toInt)
    val hours = value.split("h").headOption.map(_.toInt)
    val minutes = value.split("m").headOption.map(_.toInt)
    val seconds = value.split("s").headOption.map(_.toInt)

    val duration = Duration.ofDays(days.getOrElse(0)).plusHours(hours.getOrElse(0)).plusMinutes(minutes.getOrElse(0)).plusSeconds(seconds.getOrElse(0))
    Some(duration)
  }
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

case class MyEvent(
                    reason: Option[String],
                    message: Option[String],
                    source: Option[String],
                    firstSeen: Option[String],
                    lastSeen: Option[String],
                    count: Int,
                    eventType: Option[String],
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
      eventType = event.`type`
    )
  }
}

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
                 eventList: List[MyEvent])

object MyPod {
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
      failedScheduling = newEvents.exists(_.reason.contains("FailedScheduling")) && pod.status.get.phase.map(_.toString).contains("Pending") &&
        pod.status.get.conditions.exists(el => el.`_type`== "PodScheduled" && el.status == "False"),
      eventList = newEvents
      //      failedScheduling = pod.metadata.uid.map { uid =>
      //        val events = k8s.events.listInNamespace(pod.namespace).filter(_.involvedObject.uid.contains(uid))
      //        events.exists(_.reason.contains("FailedScheduling"))
      //      }.getOrElse(false)
    )
  }
}

case class MyNode(name: String,
                  status: Map[String, Boolean],
                  pods: List[MyPod],
                  ip: Map[String, String],
                  allocatedResources: Map[String, BigDecimal],
                  capacity: Map[String, BigDecimal],
                  allocatable: Map[String, BigDecimal])

object MyNode {
  def fromNode(node: Node, pods: List[Pod], events: List[Event]): MyNode = {


    val newPods = pods.map(MyPod.fromPod(_, events))
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
      pods = newPods.filterNot(_.isSystem),
      ip = node.status.get.addresses.map(a => a.`_type` -> a.address).toMap,
      allocatedResources = newPods.flatMap(_.allocatedResources).groupBy(_._1).map { case (k, v) => k -> v.map(_._2).sum },

      capacity = node.status.get.capacity.map((k, v) => k -> v.amount),
      allocatable = node.status.get.allocatable.map((k, v) => k -> v.amount),
    )
  }
}

case class KuberInfo(nodes: Map[String, MyNode], podsWithoutNode: List[MyPod])

object KuberInfo {
  def fromNodesAndPods(nodes: List[Node], pods: List[Pod], events: List[Event]): KuberInfo = {
    val myNodes = nodes.map { node =>
      val nodePods = pods.filter(_.spec.get.nodeName.equals(node.name))
      MyNode.fromNode(node, nodePods, events)
    }.map(n => n.name -> n).toMap
    val podsWithoutNode = pods.filter(_.spec.get.nodeName.isEmpty).filterNot(_.namespace.equals("kube-system")).map(MyPod.fromPod(_, events))
    KuberInfo(myNodes, podsWithoutNode)
  }
}

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val myEventFormat: RootJsonFormat[MyEvent] = jsonFormat7(MyEvent.apply)
  implicit val myPodFormat: RootJsonFormat[MyPod] = jsonFormat14(MyPod.apply)
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
//    val events = Await.result(Future.sequence(namespaces.items.map(el => k8s.list[EventList](Some(el.name)))), 10.seconds).flatten
    val events = Await.result(k8s.list[EventList](), 10.seconds)
//    val podEventsSelector = LabelSelector(LabelSelector.IsEqualRequirement("involvedObject.kind", "Pod"), LabelSelector.IsEqualRequirement("involvedObject.name", podName))

//    val newEvents = events.map(MyEvent.fromEvent)
//    newEvents.toJson.prettyPrint.foreach(println)
    val info = KuberInfo.fromNodesAndPods(nodes, pods, events)
    //    pods.filter(_.spec.get.nodeName.isEmpty).filterNot(_.namespace.equals("kube-system")).map(_.spec.get.containers.flatMap(_.resources)).foreach(println)

    info.toJson.prettyPrint.foreach(println)


  } catch {
    case e: Exception => throw e
  } finally {
    system.terminate()
  }
}