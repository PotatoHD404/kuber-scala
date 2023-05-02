package terraform.resources

// Security resources
case class SecurityGroup(name: String) extends InfrastructureResource {
  override def toHCL: String =
    s"""resource "aws_security_group" "$name" {
       |  name = "$name"
       |}""".stripMargin
}