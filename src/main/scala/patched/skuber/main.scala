package patched.skuber

import akka.actor.ActorSystem
import cats.effect.{ExitCode, IO}
import cats.syntax.all.*
// cats
import patched.skuber.custom.KuberInfo
import skuber.api.client.KubernetesClient
import skuber.json.format.*
import skuber.{EventList, NamespaceList, NodeList, PodList, k8sInit, toList}

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

    for {
      nodes <- IO.fromFuture(IO(k8s.list[NodeList]()))
      namespaces <- IO.fromFuture(IO(k8s.list[NamespaceList]()))
      pods <- namespaces.items.traverse(ns => IO.fromFuture(IO(k8s.list[PodList](Some(ns.name))))).map(_.flatten)
      events <- IO.fromFuture(IO(k8s.list[EventList]()))
      info = KuberInfo.fromNodesAndPods(nodes, pods, events, namespaces)

      // Cordon the node
      _ <- nodeNames.traverse(cordonNode)

      // Drain the node
      _ <- drainNodes(nodeNames, gracePeriod)

      // Other logic...
    } yield ExitCode.Success
  }
  catch {
    case e: Exception => throw e
  }
  finally {
    k8s.close
    system.terminate
  }
}