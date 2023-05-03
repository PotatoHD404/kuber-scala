package skuber

import akka.actor.ActorSystem //fs2
// cats
import skuber.api.client.KubernetesClient
import skuber.custom.KuberInfo
import skuber.json.format.*
import skuber.{EventList, NamespaceList, NodeList, PodList, cordonNode, drainNodes, k8sInit, toList}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContextExecutor, Future}

@main
def main(): Unit = {

  implicit val system: ActorSystem = ActorSystem()
  implicit val dispatcher: ExecutionContextExecutor = system.dispatcher

  implicit val k8s: KubernetesClient = k8sInit


  try {


    val nodes = Await.result(k8s.list[NodeList](), 10.seconds)

    val namespaces = Await.result(k8s.list[NamespaceList](), 10.seconds)
    val pods = Await.result(Future.sequence(namespaces.items.map(el => k8s.list[PodList](Some(el.name)))), 10.seconds).flatten
    val events = Await.result(k8s.list[EventList](), 10.seconds)

    var info = KuberInfo.fromNodesAndPods(nodes, pods, events, namespaces)

    val nodeNames = List("multinode-demo-m02")
    val gracePeriod = 30 // Adjust the grace period as needed

    //    Cordon the node
    Await.result(Future.sequence(nodeNames.map(cordonNode)), 1.minute)

    //    Drain the node
    Await.result(drainNodes(nodeNames, gracePeriod), 10.minutes)
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

  }
  catch {
    case e: Exception => throw e
  }
  finally {
    k8s.close
    system.terminate
  }
}