import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.client.RequestBuilding.Patch
import akka.stream.scaladsl.{Flow, Keep, Sink}
import akka.stream.{ActorMaterializer, FlowShape, Graph}
import jdk.internal.net.http.common.Log.logError
import play.api.libs.json.{Format, JsPath}
import skuber.*
import skuber.Pod.{Affinity, Phase, Template}
import skuber.Pod.Affinity.NodeAffinity.PreferredSchedulingTerm
import skuber.Pod.Affinity.{NodeSelectorOperator, NodeSelectorRequirement, NodeSelectorTerm}
import skuber.api.client.EventType.EventType
import skuber.api.client.{EventType, KubernetesClient, WatchEvent}
import skuber.apps.v1.ReplicaSet.{Spec, Status}
import skuber.autoscaling.v2beta1.HorizontalPodAutoscaler
//import skuber.ext.{ReplicaSet, ReplicaSetList}
//import skuber.apps.DeploymentList

import scala.concurrent.TimeoutException
//import skuber.apps.DeploymentList
import skuber.apps.v1.{Deployment, DeploymentList, StatefulSetList, StatefulSet}
import skuber.apps.v1._
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
      failedScheduling = newEvents.exists(_.reason.contains("FailedScheduling")) && pod.status.get.phase.contains(Phase.Pending) &&
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
    //    val test = Await.result(k8s.list[DeploymentList](), 10.seconds)
    //    //
    //    test.items.foreach(el => {
    //      println(el)
    //    })

    def cordonNode(nodeName: String) = {
      for {
        node <- k8s.get[Node](nodeName)
        newNode = node.copy(spec = node.spec.map(_.copy(unschedulable = true)))
        updatedNode <- k8s.update(newNode)
      } yield updatedNode
    }

    def deletePod(pod: Pod, gracePeriod: Int): Future[Unit] = {
      val deleteOptions = DeleteOptions(gracePeriodSeconds = Some(gracePeriod))
      k8s.deleteWithOptions[Pod](pod.name, deleteOptions, Some(pod.namespace))
    }

    def scaleDeployments(namespace: String, factor: Int): Future[Unit] = {
      for {
        deploymentList <- k8s.list[DeploymentList](Some(namespace))
        _ <- Future.sequence(
          deploymentList.items.map { deployment =>
            val newReplicas = deployment.spec.flatMap(_.replicas).getOrElse(0) + factor
            val updatedDeployment = deployment.copy(spec = deployment.spec.map(_.copy(replicas = Some(newReplicas))))
            k8s.update(updatedDeployment)
          }
        )
      } yield ()
    }


    def waitUntilAllPodsRunning(statefulSets: List[StatefulSet],
                                deployments: List[Deployment],
                                replicaSets: List[ReplicaSet],
                                pollInterval: FiniteDuration = 5.seconds,
                                timeout: FiniteDuration = 5.minutes
                               ): Future[Unit] = {
      val deadline = timeout.fromNow
      println(s"Waiting for pods to be running. Deployments: ${deployments.length}, StatefulSets: ${statefulSets.length}, ReplicaSets: ${replicaSets.length}")
      println(deployments)
      def checkPods(): Future[Boolean] = {
        for {
          podList <- k8s.list[PodList]()
        } yield {
          val allResourceNames = statefulSets.map(_.metadata.name) ++
            deployments.map(_.metadata.name) ++
            replicaSets.map(_.metadata.name)

          val relevantPods = podList.items.filter(pod =>
            allResourceNames.contains(pod.metadata.labels("app"))
          )

          val expectedPodCount = statefulSets.map(_.spec.map(_.replicas.getOrElse(0)).getOrElse(0)).sum +
            deployments.map(_.spec.map(_.replicas.getOrElse(0)).getOrElse(0)).sum +
            replicaSets.map(_.spec.map(_.replicas.getOrElse(0)).getOrElse(0)).sum

          println(s"Relevant pod count: ${relevantPods.length}, Expected pod count: $expectedPodCount")

          val correctPodCount = relevantPods.length == expectedPodCount
          val allPodsRunning = relevantPods.forall(_.status.exists(_.phase.contains(Phase.Running)))

          correctPodCount && allPodsRunning
        }
      }

      def poll(): Future[Unit] = {
        if (deadline.isOverdue()) {
          Future.failed(new TimeoutException(s"Timed out waiting for all pods to be running after $timeout"))
        } else {
          checkPods().flatMap { allRunning =>
            if (allRunning) {
              Future.successful(())
            } else {
              akka.pattern.after(pollInterval, system.scheduler)(poll())
            }
          }
        }
      }

      poll()
    }

    //noinspection DuplicatedCode
    def increaseReplicas[T <: ObjectResource](resource: T, increment: Int): T = {
      resource match {
        case statefulSet: StatefulSet =>
          statefulSet.copy(spec = statefulSet.spec.map(_.copy(replicas = Some(statefulSet.spec.flatMap(_.replicas).getOrElse(0) + increment)))).asInstanceOf[T]
        case deployment: Deployment =>
          deployment.copy(spec = deployment.spec.map(_.copy(replicas = Some(deployment.spec.flatMap(_.replicas).getOrElse(0) + increment)))).asInstanceOf[T]
        case replicaSet: ReplicaSet =>
          replicaSet.copy(spec = replicaSet.spec.map(_.copy(replicas = Some(replicaSet.spec.flatMap(_.replicas).getOrElse(0) + increment)))).asInstanceOf[T]
        //        case daemonSet: DaemonSet =>
        //          daemonSet.copy(spec = daemonSet.spec.map(_.copy(updateStrategy = daemonSet.spec.flatMap(_.updateStrategy).map(_.copy(rollingUpdate = daemonSet.spec.flatMap(_.updateStrategy.flatMap(_.rollingUpdate)).map(_.copy(maxUnavailable = 0))))))).asInstanceOf[T]
        case _ => resource
      }
    }

    implicit lazy val depFormat: Format[ReplicaSet] = (objFormat and
      (JsPath \ "spec").formatNullable[Spec] and
      (JsPath \ "status").formatNullable[Status])(ReplicaSet.apply, dp => (dp.kind, dp.apiVersion, dp.metadata, dp.spec, dp.status))

    implicit val deployListFormat: Format[ReplicaSetList] = ListResourceFormat[ReplicaSet]

    implicit val deployDef: ResourceDefinition[ReplicaSet] = new ResourceDefinition[ReplicaSet] {
      def spec: ResourceSpecification = ReplicaSet.specification
    }
    implicit val deployListDef: ResourceDefinition[ReplicaSetList] = new ResourceDefinition[ReplicaSetList] {
      def spec: ResourceSpecification = ReplicaSet.specification
    }

    def getResources: Future[(StatefulSetList, DeploymentList, ReplicaSetList)] = {
      for {
        statefulSets <- k8s.list[StatefulSetList]()
        deployments <- k8s.list[DeploymentList]()
        replicaSets <- k8s.list[ReplicaSetList]()
        //        daemonSets <- k8s.list[DaemonSetList]()
      } yield (statefulSets, deployments, replicaSets)
    }

    def getAutoscaler(namespace: String, deploymentName: String): Future[Option[HorizontalPodAutoscaler]] = {
      k8s.getOption[HorizontalPodAutoscaler](deploymentName, Some(namespace))
    }

    def disableAutoscaler(namespace: String, deploymentName: String): Future[Option[HorizontalPodAutoscaler]] = {
      for {
        maybeHPA <- getAutoscaler(namespace, deploymentName)
        _ <- maybeHPA.map(hpa => k8s.delete[HorizontalPodAutoscaler](hpa.name, namespace = Some(namespace))).getOrElse(Future.successful(()))
      } yield {
        maybeHPA
      }
    }

    def enableAutoscaler(hpa: HorizontalPodAutoscaler): Future[HorizontalPodAutoscaler] = {
      k8s.create(hpa, Some(hpa.namespace))
    }


    def drainNode(nodeName: String, gracePeriod: Int): Future[Unit] = {
      for {

        podList <- k8s.list[PodList]()
        nodePods = podList.items.filter(_.spec.exists(_.nodeName == nodeName))

        (statefulSets, deployments, replicaSets) <- getResources

        updatedStatefulSets = statefulSets.items.flatMap { ss =>

          val increment = nodePods.count(pod => pod.metadata.labels.get("app.kubernetes.io/managed-by").contains("statefulset-controller") && pod.metadata.labels.get("app.kubernetes.io/name") == ss.metadata.labels.get("app.kubernetes.io/name"))
          println(s"Found $increment pods for statefulset ${ss.metadata.name}")
          increment match {
            case 0 => None
            case _ => Some(increaseReplicas[StatefulSet](ss, increment))
          }
        }
        updatedDeployments = deployments.items.flatMap { d =>
          println(nodePods.length)
          nodePods.foreach(p => println(p.metadata.labels))
          println(d.metadata.labels)
          val increment = nodePods.count(pod => pod.metadata.labels.get("app.kubernetes.io/managed-by").contains("deployment-controller") && pod.metadata.labels.get("app.kubernetes.io/name") == d.metadata.labels.get("app.kubernetes.io/name"))
          println(s"Found $increment pods for deployment ${d.metadata.name}")
          increment match {
            case 0 => None
            case _ => Some(increaseReplicas[Deployment](d, increment))
          }
        }
        updatedReplicaSets = replicaSets.items.flatMap { rs =>
          val increment = nodePods.count(pod => pod.metadata.labels.get("app.kubernetes.io/managed-by").contains("replicaset-controller") && pod.metadata.labels.get("app.kubernetes.io/name") == rs.metadata.labels.get("app.kubernetes.io/name"))
          println(s"Found $increment pods for replicaset ${rs.metadata.name}")
          increment match {
            case 0 => None
            case _ => Some(increaseReplicas[ReplicaSet](rs, increment))
          }
        }
        //noinspection DuplicatedCode
        autoscalersData: List[(String, String)] = (updatedReplicaSets.map(rs => (rs.metadata.namespace,
          rs.metadata.labels.get("app.kubernetes.io/name"))).filter(_._2.isDefined).map(t => (t._1, t._2.get)) ++
          updatedDeployments.map(d => (d.metadata.namespace,
            d.metadata.labels.get("app.kubernetes.io/name"))).filter(_._2.isDefined).map(t => (t._1, t._2.get)) ++
          updatedStatefulSets.map(ss => (ss.metadata.namespace,
            ss.metadata.labels.get("app.kubernetes.io/name"))).filter(_._2.isDefined).map(t => (t._1, t._2.get))).distinct

        disabledAutoscalers <- Future.sequence(autoscalersData.map(t => disableAutoscaler(t._1, t._2)))

        filteredDisabledAutoscalers = disabledAutoscalers.filter(_.isDefined).map(_.get)

        //        updatedStatefulSets


        _ <- Future.sequence(updatedStatefulSets.map(k8s.update(_)))
        _ <- Future.sequence(updatedDeployments.map(k8s.update(_)))
        _ <- Future.sequence(updatedReplicaSets.map(k8s.update(_)))


        _ <- waitUntilAllPodsRunning(updatedStatefulSets, updatedDeployments, updatedReplicaSets)
        _ <- Future.sequence(nodePods.map(pod => deletePod(pod, gracePeriod)))

        _ <- Future.sequence(filteredDisabledAutoscalers.map(enableAutoscaler))
      } yield ()
    }

    val nodeName = "multinode-demo-m02"
    val gracePeriod = 30 // Adjust the grace period as needed

    //    Cordon the node
    Await.result(cordonNode(nodeName), 1.minute)

    //    Drain the node
    Await.result(drainNode(nodeName, gracePeriod), 10.minutes)
    //    println(s"Updated Deployments:")
    //    results.foreach(result => println(s"  - ${result.metadata.name}"))

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
    k8s.close
    system.terminate
  }
}