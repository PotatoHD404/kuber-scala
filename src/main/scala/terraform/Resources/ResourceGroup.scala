package terraform.Resources

case class ResourceGroup(name: String) extends TerraformResource {
  override def toHCL: String =
    s"""resource "azurerm_resource_group" "$name" {
       |  name     = "$name"
       |  location = "West Europe"
       |}""".stripMargin
}
