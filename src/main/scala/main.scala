import skuber._
import skuber.json.format._
import akka.actor.ActorSystem
import skuber.apps.DeploymentList

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContextExecutor}

import spray.json._
import DefaultJsonProtocol._

case class MyPod(ip: String, name: String, status: String)
object MyPod {
  def fromPod(pod: Pod): MyPod = {
    MyPod(
      ip = pod.status.get.podIP.getOrElse(""),
      name = pod.name,
      status = pod.status.get.phase.get.toString
    )
  }
}

case class MyNode(name: String, status: Map[String, String], pods: List[MyPod], ips : List[String])
object MyNode {
  def fromNode(node: Node, pods: List[Pod]): MyNode = {
    MyNode(
      name = node.name,
      status = node.status.get.conditions.map { condition =>
        condition.`_type` -> condition.status
      }.toMap,
      pods = pods.map(MyPod.fromPod),
      ips = node.status.get.addresses.map(_.address)
    )
  }
}

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val myPodFormat: RootJsonFormat[MyPod] = jsonFormat3(MyPod.apply)
  implicit val myNodeFormat: RootJsonFormat[MyNode] = jsonFormat4(MyNode.apply)
}

@main
def main(): Unit = {
  import MyJsonProtocol._

  implicit val system: ActorSystem = ActorSystem()
  implicit val dispatcher: ExecutionContextExecutor = system.dispatcher

  val k8s = k8sInit

  try {
    // List nodes
    val nodes = Await.result(k8s.list[NodeList](), 10.seconds)
    println("Nodes:")
    println()

    // List pods
    val pods = Await.result(k8s.list[PodList](Some("kube-system")), 10.seconds)

    // Convert Node and Pod objects to MyNode and MyPod case classes
    val myNodes = nodes.items.map { node =>
      val nodePods = pods.items.filter(_.spec.get.nodeName.equals(node.name))
      MyNode.fromNode(node, nodePods)
    }

    // Serialize MyNode case class objects to JSON strings
    val myNodeJsonStrings = myNodes.map(_.toJson.prettyPrint)
    myNodeJsonStrings.foreach(println)

  } catch {
    case e: Exception => throw e
  } finally {
    system.terminate()
  }
}