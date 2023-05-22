package patched.skuber

import cats.effect.*
import cats.implicits.*
import cats.syntax.flatMap.*
import cats.syntax.functor.*
import cats.syntax.option.*
import fs2.Stream
import skuber.*
import skuber.LabelSelector.dsl.{reqToSel, strToReq}
import skuber.Pod.Affinity.NodeSelectorOperator
import skuber.Pod.Phase
import skuber.api.client.EventType.EventType
import skuber.api.client.{EventType, KubernetesClient, LoggingConfig, LoggingContext}
import skuber.apps.v1.*
import skuber.autoscaling.v2beta1.{HorizontalPodAutoscaler, HorizontalPodAutoscalerList}
import skuber.json.format.*

import java.time.{Duration, ZonedDateTime}
import java.util.Optional
import scala.annotation.tailrec
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future, TimeoutException}
import scala.concurrent.duration.*
import patched.skuber.Conversions.*
import play.api.libs.json.Format


def cordonNode(nodeName: String)(implicit k8s: KubernetesClient): IO[Node] = {
  for {
    node <- IO.fromFuture(IO(k8s.get[Node](nodeName)))
    newNode = node.copy(spec = node.spec.map(_.copy(unschedulable = true)))
    updatedNode <- IO.fromFuture(IO(k8s.update(newNode)))
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
                            )(implicit k8s: KubernetesClient, temporal: Temporal[IO]): IO[Unit] = {

  val deadline = temporal.realTime.map(_ + timeout)

  println(s"Waiting for pods to be running. Deployments: ${deployments.length}, StatefulSets: ${statefulSets.length}, ReplicaSets: ${replicaSets.length}")

  def checkPods(): IO[Boolean] = {
    IO.fromFuture(IO {
      k8s.list[PodList]()
    }).map { podList =>
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

  def poll(deadline: FiniteDuration): IO[Unit] = {
    checkPods().flatMap { allRunning =>
      if (allRunning) {
        IO.unit
      } else {
        temporal.realTime.flatMap { now =>
          if (now >= deadline) {
            IO.raiseError(new TimeoutException(s"Timed out waiting for all pods to be running after $timeout"))
          } else {
            temporal.sleep(pollInterval) *> poll(deadline)
          }
        }
      }
    }
  }

  deadline.flatMap(poll)
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


def getResources(implicit k8s: KubernetesClient): IO[(StatefulSetList, DeploymentList, ReplicaSetList)] = {
  (
    k8s.list[StatefulSetList]().toIO,
    k8s.list[DeploymentList]().toIO,
    k8s.list[ReplicaSetList]().toIO
  ).tupled
}

def getAutoscaler(namespace: String, deploymentName: String)(implicit k8s: KubernetesClient): IO[Option[HorizontalPodAutoscaler]] = {
  k8s.getOption[HorizontalPodAutoscaler](deploymentName, Some(namespace)).toIO
}

def disableAutoscaler(namespace: String, deploymentName: String)(implicit k8s: KubernetesClient): IO[Option[HorizontalPodAutoscaler]] = {
  for {
    maybeHPA <- getAutoscaler(namespace, deploymentName)
    _ <- maybeHPA
      .map(hpa => IO.fromFuture(IO(k8s.delete[HorizontalPodAutoscaler](hpa.name, namespace = Some(namespace)))))
      .getOrElse(IO.unit)
  } yield maybeHPA
}

def enableAutoscaler(hpa: HorizontalPodAutoscaler)(implicit k8s: KubernetesClient): IO[HorizontalPodAutoscaler] = {
  k8s.create(hpa, Some(hpa.namespace)).toIO
}

def deletePod(pod: Pod, gracePeriod: Int)(implicit k8s: KubernetesClient): IO[Unit] = {
  val deleteOptions = DeleteOptions(gracePeriodSeconds = Some(gracePeriod))
  k8s.deleteWithOptions[Pod](pod.name, deleteOptions, Some(pod.namespace)).void.toIO
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


def drainNodes(nodeNames: List[String], gracePeriod: Int)(implicit k8s: KubernetesClient): IO[Unit] = {

  def updateResource[T <: ObjectResource : play.api.libs.json.Format : skuber.ResourceDefinition](resource: T, increment: Int): IO[T] = {
    k8s.get[T](resource.name, Some(resource.namespace)).toIO
      .flatMap(fetchedResource => k8s.update(increaseReplicas(fetchedResource, -increment)).toIO)
  }

  for {
    podList <- k8s.list[PodList]().toIO
    nodePods = podList.items.filter(_.spec.exists(el => nodeNames.contains(el.nodeName)))
    resourceLists <- getResources
    (statefulSets, deployments, replicaSets) = resourceLists

    (updatedDeployments, deploymentsIncrements) = processResources(deployments.items, nodePods)
    deploymentNames = updatedDeployments.map(el => Some(el.metadata.name))
    (updatedReplicaSets, replicaSetsIncrements) = processResources(replicaSets.items, nodePods, deploymentNames)
    (updatedStatefulSets, statefulSetsIncrements) = processResources(statefulSets.items, nodePods, deploymentNames)

    autoscalers <- k8s.list[HorizontalPodAutoscalerList]().toIO
    disabledAutoscalers <- autoscalers.map(hpa => disableAutoscaler(hpa.metadata.namespace, hpa.metadata.name)).sequence
    filteredDisabledAutoscalers = disabledAutoscalers.filter(_.isDefined).map(_.get)

    updatedStatefulSets <- updatedStatefulSets.map(ss => k8s.update(ss).toIO).sequence
    updatedDeployments <- updatedDeployments.map(d => k8s.update(d).toIO).sequence
    updatedReplicaSets <- updatedReplicaSets.map(rs => k8s.update(rs).toIO).sequence

    _ <- waitUntilAllPodsVerified(updatedStatefulSets, updatedDeployments, updatedReplicaSets)

    _ <- nodePods.map(pod => deletePod(pod, gracePeriod)).sequence

    _ <- waitUntilAllPodsVerified(updatedStatefulSets, updatedDeployments, updatedReplicaSets, checkRunning = false)

    updatedStatefulSets <- updatedStatefulSets.zip(statefulSetsIncrements).map((updateResource[StatefulSet] _).tupled).sequence
    updatedDeployments <- updatedDeployments.zip(deploymentsIncrements).map((updateResource[Deployment] _).tupled).sequence
    updatedReplicaSets <- updatedReplicaSets.zip(replicaSetsIncrements).map((updateResource[ReplicaSet] _).tupled).sequence

    _ <- waitUntilAllPodsVerified(updatedStatefulSets, updatedDeployments, updatedReplicaSets)

    _ <- filteredDisabledAutoscalers.map(enableAutoscaler).sequence
  } yield ()
}