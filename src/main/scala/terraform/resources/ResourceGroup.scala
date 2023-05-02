package terraform.resources

case class ResourceGroup(name: String) extends InfrastructureResource {
  override def toHCL: String =
    s"""resource "azurerm_resource_group" "$name" {
       |  name     = "$name"
       |  location = "West Europe"
       |}""".stripMargin
}
