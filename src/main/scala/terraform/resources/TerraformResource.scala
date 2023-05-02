package terraform.resources

abstract class TerraformResource {
  def toHCL: String
}

trait InfrastructureResource extends TerraformResource