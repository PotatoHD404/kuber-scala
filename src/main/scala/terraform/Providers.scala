package terraform

import terraform.Resources.TerraformResource

sealed trait ProviderType { def name: String }
case object AWS extends ProviderType { val name = "aws" }
case object AzureRM extends ProviderType { val name = "azurerm" }