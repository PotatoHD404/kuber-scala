package terraform.resources

case class SecurityRule(name: String, securityGroup: SecurityGroup, protocol: String, source: String, destination: String, portRange: String) extends InfrastructureResource {
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