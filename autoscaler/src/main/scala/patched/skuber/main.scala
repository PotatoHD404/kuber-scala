package patched.skuber

import akka.actor.ActorSystem
import cats.effect.{ExitCode, IO}
import cats.syntax.all.*
import patched.skuber.operations.{checkResourceUsageAndScale, cordonNode, drainNodes}
// cats
import patched.skuber.custom.KuberInfo
import skuber.api.client.KubernetesClient
import skuber.json.format.*
import skuber.{EventList, NamespaceList, NodeList, PodList, k8sInit, toList}

import patched.skuber.operations.Conversions.toIO

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContextExecutor, Future}

@main
def main(): Unit = {

  implicit val system: ActorSystem = ActorSystem()
  implicit val dispatcher: ExecutionContextExecutor = system.dispatcher

  implicit val k8s: KubernetesClient = k8sInit


  try {
    val nodeNames = List("multinode-demo-m02")
    val gracePeriod = 30 // Adjust the grace period as needed

    val checkInterval = 15.seconds
    val checkResourceUsageAndScaleIO = checkResourceUsageAndScale() 
    val scheduledCheck = IO.sleep(checkInterval) *> checkResourceUsageAndScaleIO.foreverM[Unit]

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