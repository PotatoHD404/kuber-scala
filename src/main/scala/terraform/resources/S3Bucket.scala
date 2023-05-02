package terraform.resources

// S3 resources
case class S3Bucket(name: String) extends InfrastructureResource {
  override def toHCL: String =
    s"""resource "aws_s3_bucket" "$name" {
       | bucket = "$name"
       |}""".stripMargin
}