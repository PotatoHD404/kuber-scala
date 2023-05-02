package terraform.resources

trait BackendResource extends TerraformResource
case class Backend() extends BackendResource {
  // TODO
  override def toHCL: String =
    s"""""".stripMargin
}
