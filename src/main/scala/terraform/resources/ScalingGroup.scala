package terraform.resources

// Scaling resources
case class ScalingGroup(name: String, minSize: Int, maxSize: Int, vm: VM) extends InfrastructureResource {
  override def toHCL: String =
    s"""resource "aws_autoscaling_group" "$name" {
       | name = "$name"
       | min_size = $minSize
       | max_size = $maxSize
       | desired_capacity = $minSize
       | launch_configuration = aws_launch_configuration.${vm.name}.name
       | vpc_zone_identifier = [aws_subnet.example.id]
       |}""".stripMargin
}