package terraform.Resources

import terraform.{AWS, AzureRM, ProviderType, Region}

//import terraform.{AWS, AzureRM, ProviderType, Region}


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