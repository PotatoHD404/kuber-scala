package skuber

import skuber.autoscaling.v2beta1.HorizontalPodAutoscaler

import scala.concurrent.{Await, ExecutionContextExecutor, Future, TimeoutException}
import akka.actor.ActorSystem

import skuber.*
import skuber.LabelSelector.dsl.strToReq
import skuber.Pod.Phase
import skuber.Pod.Affinity.NodeSelectorOperator
import skuber.api.client.EventType.EventType
import skuber.api.client.{EventType, KubernetesClient}
import skuber.autoscaling.v2beta1.{HorizontalPodAutoscaler, HorizontalPodAutoscalerList}

import scala.language.reflectiveCalls
import skuber.apps.v1.*
import skuber.json.format.*
import spray.json.DefaultJsonProtocol.*

import java.time.{Duration, ZonedDateTime}
import java.util.Optional
import scala.concurrent.duration.*
import reflect.Selectable.reflectiveSelectable
import skuber.LabelSelector.dsl.reqToSel

import concurrent.ExecutionContext.Implicits.global

def cordonNode(nodeName: String)(implicit k8s: KubernetesClient): Future[Node] = {
  for {
    node <- k8s.get[Node](nodeName)
    newNode = node.copy(spec = node.spec.map(_.copy(unschedulable = true)))
    updatedNode <- k8s.update(newNode)
  } yield updatedNode
}


//    def deletePod(pod: Pod)(implicit k8s: KubernetesClient): Future[Unit] = {
//
//
//      if (deploymentNameOpt.isDefined) {
//        for {
//          _ <- setDeleteFirstLabel(pod)
//          _ <- getOrCreatePodDisruptionBudget(namespace, s"${deploymentNameOpt.get}-pdb")
//        } yield ()
//      } else {
//        Future.successful(())
//      }
//    }


//    def scaleDeployments(namespace: String, factor: Int)(implicit k8s: KubernetesClient): Future[Unit] = {
//      for {
//        deploymentList <- k8s.list[DeploymentList](Some(namespace))
//        _ <- Future.sequence(
//          deploymentList.items.map { deployment =>
//            val newReplicas = deployment.spec.flatMap(_.replicas).getOrElse(0) + factor
//            val updatedDeployment = deployment.copy(spec = deployment.spec.map(_.copy(replicas = Some(newReplicas))))
//            k8s.update(updatedDeployment)
//          }
//        )
//      } yield ()
//    }


def waitUntilAllPodsVerified(statefulSets: List[StatefulSet],
                             deployments: List[Deployment],
                             replicaSets: List[ReplicaSet],
                             pollInterval: FiniteDuration = 1.seconds,
                             timeout: FiniteDuration = 5.minutes,
                             checkRunning: Boolean = true
                            )(implicit k8s: KubernetesClient, system: ActorSystem): Future[Unit] = {
  val deadline = timeout.fromNow
  println(s"Waiting for pods to be running. Deployments: ${deployments.length}, StatefulSets: ${statefulSets.length}, ReplicaSets: ${replicaSets.length}")

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
      if (!checkRunning) {
        correctPodCount
      } else {
        val allPodsRunning = relevantPods.forall(_.status.exists(_.phase.contains(Phase.Running)))

        correctPodCount && allPodsRunning
      }
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
    case _ => resource
  }
}

def getResources(implicit k8s: KubernetesClient): Future[(StatefulSetList, DeploymentList, ReplicaSetList)] = {
  for {
    statefulSets <- k8s.list[StatefulSetList]()
    deployments <- k8s.list[DeploymentList]()
    replicaSets <- k8s.list[ReplicaSetList]()
    //        daemonSets <- k8s.list[DaemonSetList]()
  } yield (statefulSets, deployments, replicaSets)
}

def getAutoscaler(namespace: String, deploymentName: String)(implicit k8s: KubernetesClient): Future[Option[HorizontalPodAutoscaler]] = {
  k8s.getOption[HorizontalPodAutoscaler](deploymentName, Some(namespace))
}

def disableAutoscaler(namespace: String, deploymentName: String)(implicit k8s: KubernetesClient): Future[Option[HorizontalPodAutoscaler]] = {
  for {
    maybeHPA <- getAutoscaler(namespace, deploymentName)
    _ <- maybeHPA.map(hpa => k8s.delete[HorizontalPodAutoscaler](hpa.name, namespace = Some(namespace))).getOrElse(Future.successful(()))
  } yield {
    maybeHPA
  }
}

def enableAutoscaler(hpa: HorizontalPodAutoscaler)(implicit k8s: KubernetesClient): Future[HorizontalPodAutoscaler] = {
  k8s.create(hpa, Some(hpa.namespace))
}


def deletePod(pod: Pod, gracePeriod: Int)(implicit k8s: KubernetesClient): Future[Unit] = {
  val deleteOptions = DeleteOptions(gracePeriodSeconds = Some(gracePeriod))
  k8s.deleteWithOptions[Pod](pod.name, deleteOptions, Some(pod.namespace))
}


def processResources[T <: ObjectResource](resources: List[T], nodePods: List[Pod], deploymentNames: List[Option[String]] = List.empty): (List[T], List[Int]) = {
  resources.flatMap { resource =>
    if (deploymentNames.nonEmpty && deploymentNames.contains(resource.metadata.labels.get("app"))) {
      None
    } else {
      val increment = nodePods.count(pod => pod.metadata.labels.get("app") == resource.metadata.labels.get("app"))
      println(s"Found $increment pods for ${resource.kind} ${resource.metadata.name}")
      increment match {
        case 0 => None
        case _ => Some((increaseReplicas(resource, increment), increment))
      }
    }
  }.unzip
}


def drainNodes(nodeNames: List[String], gracePeriod: Int)(implicit k8s: KubernetesClient, system: ActorSystem):
Future[Unit] = {
  for {

    podList <- k8s.list[PodList]()
    nodePods = podList.items.filter(_.spec.exists(el => nodeNames.contains(el.nodeName)))

    (statefulSets, deployments, replicaSets) <- getResources


    (updatedDeployments, deploymentsIncrements) = processResources(deployments.items, nodePods)
    deploymentNames = updatedDeployments.map(el => Some(el.metadata.name))
    (updatedReplicaSets, replicaSetsIncrements) = processResources(replicaSets.items, nodePods, deploymentNames)
    (updatedStatefulSets, statefulSetsIncrements) = processResources(statefulSets.items, nodePods, deploymentNames)

    autoscalers <- k8s.list[HorizontalPodAutoscalerList]()
    disabledAutoscalers <- Future.sequence(autoscalers.map(hpa => disableAutoscaler(hpa.metadata.namespace, hpa.metadata.name)))

    filteredDisabledAutoscalers = disabledAutoscalers.filter(_.isDefined).map(_.get)

    updatedStatefulSets <- Future.sequence(updatedStatefulSets.map(k8s.update(_)))
    updatedDeployments <- Future.sequence(updatedDeployments.map(k8s.update(_)))
    updatedReplicaSets <- Future.sequence(updatedReplicaSets.map(k8s.update(_)))


    _ <- waitUntilAllPodsVerified(updatedStatefulSets, updatedDeployments, updatedReplicaSets)

    _ <- Future.sequence(nodePods.map(pod => deletePod(pod, gracePeriod)))

    _ <- waitUntilAllPodsVerified(updatedStatefulSets, updatedDeployments, updatedReplicaSets, checkRunning = false)
    //noinspection DuplicatedCode
    updatedStatefulSets <- Future.sequence(updatedStatefulSets.zip(statefulSetsIncrements).map { (resource, increment) =>
      k8s.get[StatefulSet](resource.name, Some(resource.namespace)).flatMap { fetchedResource =>
        k8s.update(increaseReplicas(fetchedResource, -increment))
      }
    })
    //noinspection DuplicatedCode
    updatedDeployments <- Future.sequence(updatedDeployments.zip(deploymentsIncrements).map { (resource, increment) =>
      k8s.get[Deployment](resource.name, Some(resource.namespace)).flatMap { fetchedResource =>
        k8s.update(increaseReplicas(fetchedResource, -increment))
      }
    })
    //noinspection DuplicatedCode
    updatedReplicaSets <- Future.sequence(updatedReplicaSets.zip(replicaSetsIncrements).map { (resource, increment) =>
      k8s.get[ReplicaSet](resource.name, Some(resource.namespace)).flatMap { fetchedResource =>
        k8s.update(increaseReplicas(fetchedResource, -increment))

      }
    })

    _ <- waitUntilAllPodsVerified(updatedStatefulSets, updatedDeployments, updatedReplicaSets)

    _ <- Future.sequence(filteredDisabledAutoscalers.map(enableAutoscaler))
  }

  yield ()
}