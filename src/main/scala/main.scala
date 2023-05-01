
sealed trait ProviderType { def name: String }
case object AWSObj extends ProviderType { val name = "aws" }
case object AzureRMObj extends ProviderType { val name = "azurerm" }

// define AWS type
type AWS = AWSObj.type

// define AzureRM type
type AzureRM = AzureRMObj.type

abstract class TerraformResource[T <: ProviderType] {
  def toHCL: String
}

trait InfrastructureResource[T <: ProviderType] extends TerraformResource[T]

trait TerraformResourceHCL[A <: InfrastructureResource[_]] {
  def toHCL(resource: A): String
}

implicit object AWSNetworkHCL extends TerraformResourceHCL[Network[AWS]] {
  def toHCL(network: Network[AWS]): String = {
    s"""resource "aws_vpc" "${network.name}" {
       |  cidr_block = "${network.cidr}"
       |}""".stripMargin
  }
}

implicit object AzureRMNetworkHCL extends TerraformResourceHCL[Network[AzureRM]] {
  def toHCL(network: Network[AzureRM]): String = {
    s"""resource "azurerm_virtual_network" "${network.name}" {
       |  name                = "${network.name}"
       |  location            = "East US"
       |  resource_group_name = azurerm_resource_group.example.name
       |  address_space       = ["${network.cidr}"]
       |}""".stripMargin
  }
}

case class Network[T <: ProviderType](name: String, cidr: String)(implicit hcl: TerraformResourceHCL[Network[T]]) extends InfrastructureResource[T] {
  def toHCL: String = hcl.toHCL(this)
}


case class TerraformConfig[T <: ProviderType](resources: List[InfrastructureResource[T]]) {
  def toHCL: String = {
//    val allResources = credentials :: backend.toList ++ resources
    resources.map(_.toHCL).mkString("\n\n")
  }
}




@main
def main(): Unit = {
  val awsNetwork = Network[AWS]("my-aws-network", "10.0.0.0/16")
  val azureNetwork = Network[AzureRM]("my-azurerm-network", "10.0.0.0/16")

  val awsConfig = TerraformConfig[AWS](List(awsNetwork))
  val azureConfig = TerraformConfig[AzureRM](List(azureNetwork))

  println(awsConfig.toHCL)
  println(azureConfig.toHCL)
}