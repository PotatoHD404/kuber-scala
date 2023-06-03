package patched.skuber.operations

import cats.effect.IO
import skuber.{DeleteOptions, Node, ObjectResource, Pod}
import skuber.api.client.KubernetesClient
import skuber.apps.v1.{Deployment, DeploymentList, ReplicaSet, ReplicaSetList, StatefulSet, StatefulSetList}
import skuber.autoscaling.v2beta1.HorizontalPodAutoscaler

import patched.skuber.operations.Conversions.toIO
import skuber.objResourceToRef
import cats.implicits.*
import skuber.json.format.*

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

def cordonNode(nodeName: String)(implicit k8s: KubernetesClient): IO[Node] = {
  for {
    node <- IO.fromFuture(IO(k8s.get[Node](nodeName)))
    newNode = node.copy(spec = node.spec.map(_.copy(unschedulable = true)))
    updatedNode <- IO.fromFuture(IO(k8s.update(newNode)))
  } yield updatedNode
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
  k8s.deleteWithOptions[Pod](pod.name, deleteOptions, Some(pod.namespace)).toIO
}
