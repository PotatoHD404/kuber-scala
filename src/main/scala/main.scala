import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.client.RequestBuilding.Patch
import akka.stream.scaladsl.{Flow, Keep, Sink}
import akka.stream.{ActorMaterializer, FlowShape, Graph}
import jdk.internal.net.http.common.Log.logError
import skuber.*
import skuber.Pod.{Affinity, Template}
import skuber.Pod.Affinity.NodeAffinity.PreferredSchedulingTerm
import skuber.Pod.Affinity.{NodeSelectorOperator, NodeSelectorRequirement, NodeSelectorTerm}
import skuber.api.client.EventType.EventType
import skuber.api.client.{EventType, KubernetesClient, WatchEvent}
//import skuber.apps.DeploymentList
import skuber.apps.v1beta2.{Deployment, DeploymentList}
import skuber.json.format.*
import spray.json.*
import spray.json.DefaultJsonProtocol.*

import java.time.{Duration, ZonedDateTime}
import java.util.Optional
import scala.concurrent.duration.*
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
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
      failedScheduling = newEvents.exists(_.reason.contains("FailedScheduling")) && pod.status.get.phase.map(_.toString).contains("Pending") &&
        pod.status.get.conditions.exists(el => el.`_type` == "PodScheduled" && el.status == "False"),
      events = newEvents.map(el => el.uid -> el).toMap,
      uid = pod.metadata.uid,
      conditions = pod.status.get.conditions.map(el => el.`_type` -> stringToBoolean(el.status)).toMap
      //      failedScheduling = pod.metadata.uid.map { uid =>
      //        val events = k8s.events.listInNamespace(pod.namespace).filter(_.involvedObject.uid.contains(uid))
      //        events.exists(_.reason.contains("FailedScheduling"))
      //      }.getOrElse(false)
    )
  }
}

def stringToBoolean(s: String): Boolean = {
  s match {
    case "True" => true
    case "False" => false
    case _ => throw new IllegalArgumentException("Invalid boolean value: " + s)
  }
}

case class MyNode(name: String,
                  status: Map[String, Boolean],
                  pods: Map[String, MyPod],
                  ip: Map[String, String],
                  allocatedResources: Map[String, BigDecimal],
                  capacity: Map[String, BigDecimal],
                  allocatable: Map[String, BigDecimal],
                  uid: String)

object MyNode {

  def fromNode(node: Node): MyNode = fromNode(node, List.empty, List.empty)

  def fromNode(node: Node, pods: List[Pod], events: List[Event]): MyNode = {


    val newPods = pods.map(MyPod.fromPod(_, events))
    val _ = node.spec.get.unschedulable

    MyNode(
      name = node.name,
      status = node.status.get.conditions.map { condition => {
        condition.`_type` -> {
          stringToBoolean(condition.status)
        }
      }

      }.toMap,
      pods = newPods.filterNot(_.isSystem).map(pod => pod.uid -> pod).toMap,
      ip = node.status.get.addresses.map(a => a.`_type` -> a.address).toMap,
      allocatedResources = newPods.flatMap(_.allocatedResources).groupBy(_._1).map { case (k, v) => k -> v.map(_._2).sum },

      capacity = node.status.get.capacity.map((k, v) => k -> v.amount),
      allocatable = node.status.get.allocatable.map((k, v) => k -> v.amount),
      uid = node.metadata.uid,
    )
  }
}

//def isRedistributable(pod: MyPod): Boolean = {
//  !pod.isSystem
//}
//
case class KuberInfo(nodes: Map[String, MyNode],
                     podsWithoutNode: Map[String, MyPod],
                     unscheduledPods: Map[String, MyPod],
                     events: List[MyEvent],
                     namespaces: Set[String])
//                     {
//  def redistributePods()(implicit k8s: KubernetesClient): Future[Unit] = {
//    // Find the nodes with redistributable pods
//    val nodesWithRedistributablePods = nodes.values.filter { node =>
//      node.pods.values.exists(isRedistributable)
//    }
//
//    // Make nodes with redistributable pods unschedulable
//    val makeNodesUnschedulable = nodesWithRedistributablePods.map { node =>
//      k8s.patch[Node](
//        node.name,
//        Patch(
//          List(
//            PatchOp.Replace(
//              "/spec/unschedulable",
//              JsBoolean(true)
//            )
//          )
//        )
//      )
//    }
//
//    // Collect redistributable pods from the nodes
//    val redistributablePods = nodesWithRedistributablePods.flatMap(_.pods.values.filter(isRedistributable)).toList
//
//    // Schedule redistributable pods to other nodes
////    val scheduleRedistributablePods = redistributablePods.map { pod =>
////      k8s.create[Pod](
////        Pod(
////          metadata = ObjectMeta(
////            name = pod.name,
////            namespace = pod.namespace
////          ),
////          spec = Some(Pod.Spec(
////            containers = pod.containers
////          ))
////        ),
////        Some(pod.namespace)
////      )
////    }
//
//    // Wait for all pods to be scheduled and running
//    val waitForPodsRunning = scheduleRedistributablePods.map { scheduledPodFut =>
//      scheduledPodFut.flatMap { scheduledPod =>
//        k8s.waitForStatus[Pod](
//          scheduledPod.metadata.name,
//          scheduledPod.metadata.namespace,
//          (pod: Pod) => pod.status.exists(_.phase.contains("Running"))
//        )
//      }
//    }
//
//    // Send termination signal to nodes with redistributable pods
//    val terminateNodes = nodesWithRedistributablePods.map { node =>
//      k8s.delete[Node](node.name)
//    }
//
//    // Execute the above steps sequentially
//    for {
//      _ <- Future.sequence(makeNodesUnschedulable)
//      _ <- Future.sequence(scheduleRedistributablePods)
//      _ <- Future.sequence(waitForPodsRunning)
//      _ <- Future.sequence(terminateNodes)
//    } yield ()
//  }
//}

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

case class MyWatchEvent(eventType: EventType,
                        pod: Option[MyPod],
                        node: Option[MyNode],
                        namespace: Option[String],
                        event: Option[MyEvent])

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val myEventFormat: RootJsonFormat[MyEvent] = jsonFormat9(MyEvent.apply)
  implicit val myPodFormat: RootJsonFormat[MyPod] = jsonFormat16(MyPod.apply)
  implicit val myNodeFormat: RootJsonFormat[MyNode] = jsonFormat8(MyNode.apply)
  implicit val kuberInfoFormat: RootJsonFormat[KuberInfo] = jsonFormat5(KuberInfo.apply)
  implicit val myWatchEventFormat: RootJsonFormat[MyWatchEvent] = jsonFormat5(MyWatchEvent.apply)
  implicit val skuberEventTypeFormat: RootJsonFormat[EventType] = new RootJsonFormat[EventType] {
    override def write(obj: EventType): JsValue = JsString(obj.toString)

    override def read(json: JsValue): EventType = EventType.withName(json.convertTo[String])
  }
}

@main
def main(): Unit = {
  import MyJsonProtocol.*

  implicit val system: ActorSystem = ActorSystem()
  implicit val dispatcher: ExecutionContextExecutor = system.dispatcher

  val k8s = k8sInit

  try {


    val nodes = Await.result(k8s.list[NodeList](), 10.seconds)

    val namespaces = Await.result(k8s.list[NamespaceList](), 10.seconds)
    val pods = Await.result(Future.sequence(namespaces.items.map(el => k8s.list[PodList](Some(el.name)))), 10.seconds).flatten
    val events = Await.result(k8s.list[EventList](), 10.seconds)

    var info = KuberInfo.fromNodesAndPods(nodes, pods, events, namespaces)

    def fetchKubernetesResources(): Future[KuberInfo] = {
      for {
        nodes <- k8s.list[NodeList]()
        namespaces <- k8s.list[NamespaceList]()
        pods <- Future.sequence(namespaces.items.map(el => k8s.list[PodList](Some(el.name)))).map(_.flatten)
        events <- k8s.list[EventList]()
      } yield KuberInfo.fromNodesAndPods(nodes, pods, events, namespaces)
    }


    val namespace = "default"
    val nodesToExclude = Set("node1", "node2")

    def updateDeploymentAntiAffinity(deployment: Deployment, nodesToExclude: Set[String]): Deployment = {
      val updatedPodSpec = deployment.spec.flatMap(_.template.flatMap(_.spec.map { spec =>
        val affinity = spec.affinity.getOrElse(Affinity())
        val updatedNodeAffinity = affinity.nodeAffinity.map { nodeAffinity =>
          val updatedPreferred = nodeAffinity.preferredDuringSchedulingIgnoredDuringExecution :+
            PreferredSchedulingTerm(
              NodeSelectorTerm(nodesToExclude.toList.map { nodeName =>
                NodeSelectorRequirement("kubernetes.io/hostname", NodeSelectorOperator.NotIn, List(nodeName))
              }),
              100
            )
          nodeAffinity.copy(preferredDuringSchedulingIgnoredDuringExecution = updatedPreferred)
        }
        spec.copy(affinity = Some(affinity.copy(nodeAffinity = updatedNodeAffinity)))
      }))
      deployment.copy(spec = deployment.spec.map(_.copy(template = deployment.spec.map(_.template.map(_.copy(spec = updatedPodSpec)).getOrElse(Template.Spec())))))
    }


    val deploymentsFut = k8s.list[DeploymentList]()
    val updatedDeploymentsFut = deploymentsFut.map(_.items.map(updateDeploymentAntiAffinity(_, nodesToExclude)))

    val resultFut = for {
      updatedDeployments <- updatedDeploymentsFut
//      results <- Future.sequence(updatedDeployments.map(k8s.update(_)))
    } yield updatedDeployments

    val results = Await.result(resultFut, 10.seconds)
    println(s"Updated Deployments:")
    results.foreach(result => println(s"  - ${result.metadata.name}"))

//    def updateInfo(): Future[Unit] = {
//      fetchKubernetesResources().flatMap { newInfo =>
//        println(newInfo.toJson.prettyPrint)
//        if (info != newInfo) {
//          info = newInfo
//
//
//        } else {
//          println("no change")
//        }
//        Future.successful(())
//      }
//    }

    // Initial fetch
//    updateInfo()

    // Schedule updates every 5 seconds
//    val updateInterval = 5.seconds
//    val scheduler = system.scheduler.scheduleAtFixedRate(updateInterval, updateInterval)(() => updateInfo())

  } catch {
    case e: Exception => throw e
  } finally {
    //    k8s.close
    //    system.terminate
  }
}