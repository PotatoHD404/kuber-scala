package terraform.Resources

case class Subnet(name: String, network: Network, cidrBlock: String) extends TerraformResource {
  override def toHCL: String =
    s"""resource "aws_subnet" "$name" {
       |  vpc_id     = aws_vpc.${network.name}.id
       |  cidr_block = "$cidrBlock"
       |}""".stripMargin
}