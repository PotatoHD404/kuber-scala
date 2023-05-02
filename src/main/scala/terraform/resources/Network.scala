package terraform.resources

import terraform.{AWS, AzureRM, CIDRBlock}

// Network resources
case class Network(name: String, cidrBlock: CIDRBlock, provider: Provider) extends InfrastructureResource {
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