package terraform.Resources

// Security resources
case class SecurityGroup(name: String) extends TerraformResource {
  override def toHCL: String =
    s"""resource "aws_security_group" "$name" {
       |  name = "$name"
       |}""".stripMargin
}