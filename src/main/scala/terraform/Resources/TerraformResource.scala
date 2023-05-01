package terraform.Resources

abstract class TerraformResource {
  def toHCL: String
}