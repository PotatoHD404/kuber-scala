
//sealed trait ProviderType { def name: String }
//case object AWSObj extends ProviderType { val name = "aws" }
//case object AzureRMObj extends ProviderType { val name = "azurerm" }
//
//// define AWS type
//type AWS = AWSObj.type
//
//// define AzureRM type
//type AzureRM = AzureRMObj.type

sealed trait ProviderType

sealed trait AWS extends ProviderType

sealed trait AzureRM extends ProviderType

abstract class TerraformResource[T <: ProviderType] {
  def toHCL: String
}

trait InfrastructureResource[T <: ProviderType] extends TerraformResource[T]

trait TerraformResourceHCL[A <: InfrastructureResource[_]] {
  def toHCL(resource: A): String
}

object TerraformResourceHCL {
  def apply[T <: InfrastructureResource[_]](implicit hcl: TerraformResourceHCL[T]): TerraformResourceHCL[T] = hcl
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

implicit object AWSVMHCL extends TerraformResourceHCL[VM[AWS]] {
  def toHCL(vm: VM[AWS]): String = {
    s"""resource "aws_instance" "${vm.name}" {
       |  ami           = "ami-0c9483f45c3bd5d27" // Replace this with your desired AMI ID
       |  instance_type = "t2.micro"
       |  vpc_security_group_ids = ["${vm.network.name}"]
       |}""".stripMargin
  }
}

implicit object AzureRMVMHCL extends TerraformResourceHCL[VM[AzureRM]] {
  def toHCL(vm: VM[AzureRM]): String = {
    s"""resource "azurerm_linux_virtual_machine" "${vm.name}" {
       |  name                  = "${vm.name}"
       |  location              = "East US"
       |  resource_group_name   = azurerm_resource_group.example.name
       |  network_interface_ids = [azurerm_network_interface.example.id]
       |  size                  = "Standard_B1s"
       |
       |  os_disk {
       |    caching              = "ReadWrite"
       |    storage_account_type = "Standard_LRS"
       |  }
       |
       |  source_image_reference {
       |    publisher = "Canonical"
       |    offer     = "UbuntuServer"
       |    sku       = "18.04-LTS"
       |    version   = "latest"
       |  }
       |
       |  computer_name  = "${vm.name}"
       |  admin_username = "adminuser"
       |  disable_password_authentication = true
       |
       |  admin_ssh_key {
       |    username   = "adminuser"
       |    public_key = file("~/.ssh/id_rsa.pub")
       |  }
       |}""".stripMargin
  }
}


case class Network[T <: ProviderType](name: String, cidr: String)(implicit hcl: TerraformResourceHCL[Network[T]]) extends InfrastructureResource[T] {
  def toHCL: String = hcl.toHCL(this)
}

case class VM[T <: ProviderType](name: String, network: Network[T])(implicit hcl: TerraformResourceHCL[VM[T]]) extends InfrastructureResource[T] {
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
  val awsVM = VM[AWS]("my-aws-vm", awsNetwork)
  val azureVM = VM[AzureRM]("my-azurerm-vm", azureNetwork)

  val awsConfig = TerraformConfig[AWS](List(awsNetwork, awsVM))
  //  val azureConfig = TerraformConfig[AzureRM](List(azureNetwork, azureVM, awsVM))
  // Found:    (awsVM : VM[AWS])
  // Required: InfrastructureResource[AzureRM]
  //   val azureConfig = TerraformConfig[AzureRM](List(azureNetwork, azureVM, awsVM))

  val azureConfig = TerraformConfig[AzureRM](List(azureNetwork, azureVM))
  println(awsConfig.toHCL)
  println(azureConfig.toHCL)
}