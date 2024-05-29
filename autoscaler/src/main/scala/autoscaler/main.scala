package autoscaler

import akka.actor.ActorSystem
import cats.effect.{ExitCode, IO}
import cats.syntax.all.*
import patched.skuber.operations.{checkResourceUsageAndScale, cordonNode, drainNodes}
import terraform.providers.yandex.yandexprovidersettings.YandexProviderSettings

import scala.io.Source
import scala.util.{Failure, Try, Using}
import patched.skuber.custom.KuberInfo
import skuber.api.client.KubernetesClient
import skuber.json.format.*
import skuber.{EventList, NamespaceList, NodeList, PodList, k8sInit, toList}
import patched.skuber.operations.Conversions.toIO
import terraform.kubenetes.clusters.{Cluster, VMConfig, YandexCluster}
import terraform.{DotenvLoader, S3Backend, createCluster, envOrError, envOrNone}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContextExecutor, Future}

@main
def main(): Unit = {

  implicit val system: ActorSystem = ActorSystem()
  implicit val dispatcher: ExecutionContextExecutor = system.dispatcher

  implicit val k8s: KubernetesClient = k8sInit
  implicit val cluster: YandexCluster[YandexProviderSettings, S3Backend] = createCluster()
  println("Starting autoscaling process")

  try {
    val nodeNames = List("multinode-demo-m02")
    val gracePeriod = 30 // Adjust the grace period as needed

    val checkInterval = 15.seconds
    val checkResourceUsageAndScaleIO = checkResourceUsageAndScale()
    val scheduledCheck = IO.sleep(checkInterval) *> checkResourceUsageAndScaleIO.foreverM

    //    for {
    //      nodes <- k8s.list[NodeList]().toIO
    //      namespaces <- k8s.list[NamespaceList]().toIO
    //      pods <- namespaces.items.traverse(ns => k8s.list[PodList](Some(ns.name)).toIO).map(_.flatten)
    //      events <- k8s.list[EventList]().toIO
    //      info = KuberInfo.fromNodesAndPods(nodes, pods, events, namespaces)
    //
    //      // Cordon the node
    //      _ <- nodeNames.traverse(cordonNode)
    //
    //      // Drain the node
    //      _ <- drainNodes(nodeNames, gracePeriod)
    //
    //      // Other logic...
    //    } yield ExitCode.Success
  }
  catch {
    case e: Exception => throw e
  }
  finally {
    k8s.close
    system.terminate
  }
}