package terraform.Resources

import terraform.{AWS, AzureRM}

// VM resources
case class VM(name: String, image: String, size: String, provider: Provider) extends InfrastructureResource {
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