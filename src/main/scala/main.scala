import akka.actor.ActorSystem
import skuber.api.client.KubernetesClient
import skuber.custom.KuberInfo
import skuber.{EventList, NamespaceList, NodeList, PodList, cordonNode, drainNodes, k8sInit, toList}

import concurrent.duration.DurationInt
import skuber.json.format.*
import terraform.*
import terraform.Resources._

import scala.concurrent.{Await, ExecutionContextExecutor, Future}

@main
def main(): Unit = {
  val provider = Provider("aws", Region("us-west-2"), AWS)
  val network = Network("example", CIDRBlock("10.0.0.0/16"), provider)
  val portRange = PortRange(22, 22)
  val credentials = Credentials(provider, "ACCESS_KEY", "SECRET_KEY")
  val subnet = Subnet("example", network, "10.0.1.0/24")
  val securityGroup = SecurityGroup("example")
  val securityRule = SecurityRule("example", securityGroup, "tcp", "0.0.0.0/0", "10.0.0.0/8", "22-22")
  val s3Bucket = S3Bucket("example")
  val vm = VM("example", "Canonical:UbuntuServer:18.04-LTS:latest", "Standard_B1s", provider)
  val scalingGroup = ScalingGroup("example", 1, 3, vm)

  val resources: List[TerraformResource] = List(provider, credentials, network, subnet, securityGroup, securityRule, s3Bucket, vm, scalingGroup)
  val hclCode = TerraformGenerator.generateHCL(resources)
  println(hclCode)
}