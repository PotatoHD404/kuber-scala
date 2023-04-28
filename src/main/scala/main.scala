import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Keep, Sink}
import akka.stream.{ActorMaterializer, FlowShape, Graph}
import jdk.internal.net.http.common.Log.logError
import skuber.*
import skuber.api.client.EventType.EventType
import skuber.api.client.{EventType, WatchEvent}
import skuber.apps.DeploymentList
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
                 uid: String)

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


// TODO: once it spontaneously crashed so need to handle exceptions properly

@main
def main(): Unit = {
  import MyJsonProtocol.*

  implicit val system: ActorSystem = ActorSystem()
  implicit val dispatcher: ExecutionContextExecutor = system.dispatcher

  val k8s = k8sInit

  try {
    // List nodes

    val nodes = Await.result(k8s.list[NodeList](), 10.seconds)

    val namespaces = Await.result(k8s.list[NamespaceList](), 10.seconds)
    val pods = Await.result(Future.sequence(namespaces.items.map(el => k8s.list[PodList](Some(el.name)))), 10.seconds).flatten
    val events = Await.result(k8s.list[EventList](), 10.seconds)

    var info = KuberInfo.fromNodesAndPods(nodes, pods, events, namespaces)
    //    var info = KuberInfo.createEmpty
    var i: Int = 0;

    //    info.toJson.prettyPrint.foreach(println)
    def processEvent: Flow[WatchEvent[_ >: Pod & Node & Event & Namespace <: ObjectResource], Unit, NotUsed] = {
      Flow[WatchEvent[_ >: Pod & Node & Event & Namespace <: ObjectResource]].map({ watchedEvent =>
        i += 1
        println(i)
        println(watchedEvent)
        val eventType = watchedEvent.`_type`

        watchedEvent.`_object` match {
          case pod: Pod => podEventHandler(eventType, MyPod.fromPod(pod))
          case node: Node => nodeEventHandler(eventType, MyNode.fromNode(node))
          case namespace: Namespace => namespaceEventHandler(eventType, namespace)
          case event: Event => eventEventHandler(eventType, MyEvent.fromEvent(event))
          case _ => throw new Exception("Unknown object")
        }
        println("ok")
        //        println(info.toJson.prettyPrint)
        //        MyWatchEvent(
        //          eventType = watchedEvent.`_type`,
        //          pod = pod,
        //          node = node,
        //          namespace = namespace,
        //          event = event
        //        )
      })
    }

    def namespaceEventHandler(eventType: EventType, namespace: Namespace): Unit = {
      println("Namespace")
      println(eventType.toJson.prettyPrint)

      val namespaceName = namespace.metadata.name
      eventType match {
        case EventType.ADDED =>
          info = info.copy(namespaces = info.namespaces + namespaceName)
        case EventType.DELETED =>
          info = info.copy(namespaces = info.namespaces - namespaceName)
        case _ =>
          println(s"Unhandled event type: $eventType")
      }
    }

    def nodeEventHandler(eventType: EventType, node: MyNode): Unit = {
      //      println("Node")
      //      println(node.toJson.prettyPrint)

      val nodeUid = node.uid
      eventType match {
        case EventType.ADDED =>
          info = info.copy(nodes = info.nodes.updated(nodeUid, node))
        case EventType.MODIFIED =>
          val prevNode = info.nodes.get(nodeUid)
          val updatedNode = prevNode.map { n =>
            node.copy(
              pods = n.pods,
              allocatedResources = n.allocatedResources,
            )
          }.getOrElse(node)
          info = info.copy(nodes = info.nodes.updated(nodeUid, updatedNode))
        case EventType.DELETED =>
          info = info.copy(nodes = info.nodes - nodeUid)
        case _ =>
          println(s"Unhandled event type: $eventType")
      }
    }

    def podEventHandler(event: EventType, pod: MyPod): Unit = {
      //      println("Pod")
      //      println(pod.toJson.prettyPrint)

      val podUid = pod.uid
      val nodeNameOpt = pod.name

      def updateNodePods(nodeUid: String, updateFn: Map[String, MyPod] => Map[String, MyPod]): Unit = {
        info.nodes.get(nodeUid).foreach { node =>
          val updatedPods = updateFn(node.pods)
          val updatedNode = node.copy(pods = updatedPods)
          info = info.copy(nodes = info.nodes.updated(nodeUid, updatedNode))
        }
      }

      event match {
        case EventType.ADDED =>
          nodeNameOpt.foreach { nodeName =>
            updateNodePods(nodeName, _ + (podUid -> pod))
          }

        case EventType.MODIFIED =>
          nodeNameOpt.foreach { nodeName =>
            updateNodePods(nodeName, { pods =>
              val updatedPod = pods.get(podUid)
                .map(existingPod => pod.copy(events = existingPod.events))
                .getOrElse(pod)
              pods.updated(podUid, updatedPod)
            })
          }

        case EventType.DELETED =>
          nodeNameOpt.foreach { nodeName =>
            updateNodePods(nodeName, _ - podUid)
          }

        case _ =>
          println(s"Unhandled event type: $event")
      }
    }

    def eventEventHandler(eventType: EventType, event: MyEvent): Unit = {
      //      println("Event")
      //      println(event.toJson.prettyPrint)

      event.podUid.foreach { podUid =>
        //        val (foundNode, pod) = info.nodes.values.find(_.pods.contains(podUid)) match {
        //          case Some(node) => (Some(node), node.pods(podUid))
        //          case None => (None, info.podsWithoutNode(podUid))
        //        }

        //        val updatedEvents = eventType match {
        //          case EventType.ADDED =>
        //            pod.eventList :+ event
        //
        //          case EventType.MODIFIED =>
        //            pod.eventList.filterNot(_.uid == event.uid) :+ event
        //
        //          case EventType.DELETED =>
        //            pod.eventList.filterNot(_.uid == event.uid)
        //
        //          case _ =>
        //            println(s"Unhandled event type: $eventType")
        //            pod.eventList
        //        }
        ////        val failedScheduling = false
        //        val failedScheduling = updatedEvents.exists(_.reason.contains("FailedScheduling")) &&
        //          pod.status.contains("Pending") &&
        //          pod.conditions.exists(el => el._1 == "PodScheduled" && !el._2)
        //
        //        val updatedPod = pod.copy(eventList = updatedEvents, failedScheduling = failedScheduling)
        //
        //        foundNode match {
        //          case Some(node) =>
        //            val updatedPods = node.pods.updated(podUid, updatedPod)
        //            val updatedNode = node.copy(pods = updatedPods)
        //            info = info.copy(nodes = info.nodes.updated(node.uid, updatedNode))
        //
        //          case None =>
        //            info = info.copy(podsWithoutNode = info.podsWithoutNode.updated(podUid, updatedPod))
        //        }
      }
    }

    val customListOptions = ListOptions(timeoutSeconds = Some(5), resourceVersion = Some("0"))

    val podWatch = k8s.watchWithOptions[Pod](customListOptions)
    val nodeWatch = k8s.watchWithOptions[Node](customListOptions)
    //    val eventWatch = k8s.watchAllContinuously[Event]()
    val namespaceWatch = k8s.watchWithOptions[Namespace](customListOptions)
    //    val allWatch = podWatch.merge(nodeWatch).merge(eventWatch).merge(namespaceWatch)
    val allWatch = podWatch.merge(nodeWatch).merge(namespaceWatch)
    allWatch
      .viaMat(processEvent)(Keep.right)
      .recover { e =>
        println("Error")
        e.printStackTrace()
        throw e
      }
      .toMat(Sink.ignore)(Keep.right)
      .run()


  } catch {
    case e: Exception => throw e
  } finally {
    //    system.terminate()
  }
}