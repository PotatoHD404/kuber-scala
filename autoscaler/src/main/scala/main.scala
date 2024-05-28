package patched.skuber

import akka.actor.ActorSystem
import cats.effect.{ExitCode, IO}
import cats.syntax.all.*
import patched.skuber.operations.{checkResourceUsageAndScale, cordonNode, drainNodes}
import terraform.kubenetes_clusters.{Cluster, VMConfig, YandexCluster}
import terraform.providers.yandex.yandexprovidersettings.YandexProviderSettings

import scala.io.Source
import scala.util.{Failure, Try, Using}
import patched.skuber.custom.KuberInfo
import skuber.api.client.KubernetesClient
import skuber.json.format.*
import skuber.{EventList, NamespaceList, NodeList, PodList, k8sInit, toList}
import patched.skuber.operations.Conversions.toIO
import terraform.{DotenvLoader, S3Backend, envOrError, envOrNone}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContextExecutor, Future}

@main
def main(): Unit = {

  implicit val system: ActorSystem = ActorSystem()
  implicit val dispatcher: ExecutionContextExecutor = system.dispatcher

  implicit val k8s: KubernetesClient = k8sInit
  DotenvLoader.load()
  val provider = YandexProviderSettings(
    cloudId = envOrNone("YC_CLOUD_ID"),
    folderId = envOrNone("YC_FOLDER_ID"),
    token = envOrNone("YC_TOKEN"),
    zone = envOrNone("YC_ZONE")
  )
  val vmConfigs = List(
    VMConfig(
      count = 2,
      cores = 2,
      memory = 4,
      diskSize = 20,
    )
  )

  val terraformFilePath = "terraformOutput.tf"
  val k3sToken = envOrError("K3S_TOKEN")
  val backendConfig = Some(
    S3Backend(
      bucketName = "autoscaler-bucket",
      stateFileKey = "terraform.tfstate",
      region = "ru-central1",
      s3Endpoint = "https://storage.yandexcloud.net",
      accessKey = envOrError("S3_ACCESS_KEY"),
      secretKey = envOrError("S3_SECRET_KEY"),
    )
  )

  implicit val cluster: YandexCluster[YandexProviderSettings, S3Backend] = YandexCluster(provider, backendConfig, vmConfigs = vmConfigs, k3sToken = k3sToken)


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