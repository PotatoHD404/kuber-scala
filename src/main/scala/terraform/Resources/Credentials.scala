package terraform.Resources

// Credentials
case class Credentials(provider: Provider, accessKey: String, secretKey: String) extends TerraformResource {
  override def toHCL: String =
    s"""provider "aws" {
       |  region     = "${provider.region}"
       |  access_key = "$accessKey"
       |  secret_key = "$secretKey"
       |}""".stripMargin
}