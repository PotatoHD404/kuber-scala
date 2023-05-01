package terraform.Resources

// S3 resources
case class S3Bucket(name: String) extends TerraformResource {
  override def toHCL: String =
    s"""resource "aws_s3_bucket" "$name" {
       | bucket = "$name"
       |}""".stripMargin
}