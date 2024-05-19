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

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContextExecutor, Future}

object DotenvLoader {
  def load(dotenvFilePath: String = ".env"): Unit = {
    Try(Using.resource(Source.fromFile(dotenvFilePath)) { source =>
      source.getLines().foreach { line =>
        val cleanLine = line.split("#").head.trim // Removes comments and trims
        if (cleanLine.nonEmpty) {
          val Array(key, value) = cleanLine.split("=", 2).map(_.trim)
          val processedValue = value.stripPrefix("\"").stripSuffix("\"") // Removes surrounding quotes if present
          System.setProperty(key, processedValue)
        }
      }
    }) match {
      case Failure(ex) =>
        println(s"Warning: Failed to load .env file from $dotenvFilePath. ${ex.getMessage}")
      case _ => // Success, do nothing
    }
  }
}

def envOrNone(name: String): Option[String] = {
  Option(System.getenv(name)).orElse(Option(System.getProperty(name)))
}

def envOrError(name: String): String = {
  Option(System.getenv(name))
    .orElse(Option(System.getProperty(name)))
    .getOrElse(throw new NoSuchElementException(s"No environment variable or system property found for '$name'"))
}

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
  implicit val cluster: Cluster = YandexCluster(provider, vmConfigs = vmConfigs)


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