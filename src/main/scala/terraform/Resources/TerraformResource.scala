package terraform.Resources

abstract class TerraformResource {
  def toHCL: String
}

trait InfrastructureResource extends TerraformResource