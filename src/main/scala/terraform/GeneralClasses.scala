package terraform

import scala.util.matching.Regex

abstract class TerraformResource {
  def toHCL: String
}

case class Region(value: String)
case class CIDRBlock(value: String) {
  private val cidrPattern: Regex = """^(\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}\/\d{1,2})$""".r
  require(cidrPattern.matches(value), s"Invalid CIDR block: $value")
}
case class PortRange(from: Int, to: Int) {
  require(from >= 0 && from <= 65535, s"Invalid from port: $from")
  require(to >= 0 && to <= 65535, s"Invalid to port: $to")
  require(from <= to, s"From port ($from) must be less than or equal to the to port ($to)")
}
sealed trait ProviderType { def name: String }
case object AWS extends ProviderType { val name = "aws" }
case object AzureRM extends ProviderType { val name = "azurerm" }
case class Image(publisher: String, offer: String, sku: String)

// General resources
case class Provider(name: String, region: Region, providerType: ProviderType) extends TerraformResource {
  override def toHCL: String = providerType match {
    case AWS => s"""provider "aws" {
                        |  region = "${region.value}"
                        |}""".stripMargin
    case AzureRM => s"""provider "azurerm" {
                            |  features {}
                            |}""".stripMargin
    case _ => throw new Exception("Unsupported provider type")
  }
}

case class ResourceGroup(name: String) extends TerraformResource {
  override def toHCL: String =
    s"""resource "azurerm_resource_group" "$name" {
       |  name     = "$name"
       |  location = "West Europe"
       |}""".stripMargin
}

// VM resources
case class VM(name: String, image: String, size: String, provider: Provider) extends TerraformResource {
  override def toHCL: String = provider.providerType match {
    case AWS => s"""resource "aws_instance" "$name" {
                     |  ami           = "$image"
                     |  instance_type = "$size"
                     |
                     |  tags = {
                     |    Name = "$name"
                     |  }
                     |}""".stripMargin
    case AzureRM => s"""resource "azurerm_virtual_machine" "$name" {
                         |  name                  = "$name"
                         |  location              = "${provider.region}"
                         |  resource_group_name   = azurerm_resource_group.example.name
                         |  vm_size               = "$size"
                         |  delete_os_disk_on_termination = true
                         |
                         |  storage_image_reference {
                         |    publisher = "${image.split(":")(0)}"
                         |    offer     = "${image.split(":")(1)}"
                         |    sku       = "${image.split(":")(2)}"
                         |    version   = "latest"
                         |  }
                         |}""".stripMargin
    case _ => throw new Exception("Unsupported provider type")
  }
}

// Credentials
case class Credentials(provider: Provider, accessKey: String, secretKey: String) extends TerraformResource {
  override def toHCL: String =
    s"""provider "aws" {
       |  region     = "${provider.region}"
       |  access_key = "$accessKey"
       |  secret_key = "$secretKey"
       |}""".stripMargin
}

// Network resources
case class Network(name: String, cidrBlock: CIDRBlock, provider: Provider) extends TerraformResource {
  override def toHCL: String = provider.providerType match {
    case AWS => s"""resource "aws_vpc" "$name" {
                     |  cidr_block = "$cidrBlock"
                     |}""".stripMargin
    case AzureRM => s"""resource "azurerm_virtual_network" "$name" {
                         |  name                = "$name"
                         |  location            = "${provider.region}"
                         |  resource_group_name = azurerm_resource_group.example.name
                         |  address_space       = ["$cidrBlock"]
                         |}""".stripMargin
    case _ => throw new Exception("Unsupported provider type")
  }
}

case class Subnet(name: String, network: Network, cidrBlock: String) extends TerraformResource {
  override def toHCL: String =
    s"""resource "aws_subnet" "$name" {
       |  vpc_id     = aws_vpc.${network.name}.id
       |  cidr_block = "$cidrBlock"
       |}""".stripMargin
}

// Security resources
case class SecurityGroup(name: String) extends TerraformResource {
  override def toHCL: String =
    s"""resource "aws_security_group" "$name" {
       |  name = "$name"
       |}""".stripMargin
}

case class SecurityRule(name: String, securityGroup: SecurityGroup, protocol: String, source: String, destination: String, portRange: String) extends TerraformResource {
  override def toHCL: String =
    s"""resource "aws_security_group_rule" "$name" {
       |  security_group_id = aws_security_group.${securityGroup.name}.id
       |  type        = "ingress"
       |  from_port   = ${portRange.split("-")(0)}
       |  to_port     = ${portRange.split("-")(1)}
       |  protocol    = "$protocol"
       |  cidr_blocks = ["$source"]
       |}""".stripMargin
}

// S3 resources
case class S3Bucket(name: String) extends TerraformResource {
  override def toHCL: String =
    s"""resource "aws_s3_bucket" "$name" {
       | bucket = "$name"
       |}""".stripMargin
}

// Scaling resources
case class ScalingGroup(name: String, minSize: Int, maxSize: Int, vm: VM) extends TerraformResource {
  override def toHCL: String =
    s"""resource "aws_autoscaling_group" "$name" {
       | name = "$name"
       | min_size = $minSize
       | max_size = $maxSize
       | desired_capacity = $minSize
       | launch_configuration = aws_launch_configuration.${vm.name}.name
       | vpc_zone_identifier = [aws_subnet.example.id]
       |}""".stripMargin
}

object TerraformGenerator {
  def generateHCL(resources: List[TerraformResource]): String = {
    resources.map(_.toHCL).mkString("\n\n")
  }
}
