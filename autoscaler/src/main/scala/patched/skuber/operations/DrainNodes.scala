package patched.skuber.operations

import skuber.ObjectResource
import skuber.api.client.KubernetesClient

import cats.effect.IO
import skuber.*
import skuber.api.client.KubernetesClient
import skuber.apps.v1.*
import skuber.autoscaling.v2beta1.HorizontalPodAutoscalerList
import skuber.objResourceToRef
import patched.skuber.operations.Conversions.toIO

import cats.implicits.toTraverseOps
import skuber.json.format.*

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