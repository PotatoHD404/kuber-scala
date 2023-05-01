package terraform

abstract class TerraformResource {
  def toHCL: String
}

// General resources
case class Provider(name: String, region: String) extends TerraformResource {
  override def toHCL: String =
    s"""provider "$name" {
       |  region = "$region"
       |}""".stripMargin
}

case class ResourceGroup(name: String) extends TerraformResource {
  override def toHCL: String =
    s"""resource "azurerm_resource_group" "$name" {
       |  name     = "$name"
       |  location = "West Europe"
       |}""".stripMargin
}

// VM resources
case class VM(name: String, image: String, size: String) extends TerraformResource {
  override def toHCL: String =
    s"""resource "azurerm_virtual_machine" "$name" {
       |  name                  = "$name"
       |  location              = "West Europe"
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
case class Network(name: String, cidrBlock: String) extends TerraformResource {
  override def toHCL: String =
    s"""resource "aws_vpc" "$name" {
       |  cidr_block = "$cidrBlock"
       |}""".stripMargin
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
