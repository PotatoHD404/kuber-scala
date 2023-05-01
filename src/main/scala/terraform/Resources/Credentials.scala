package terraform.Resources

trait CredentialsResource extends TerraformResource
// Credentials
case class Credentials(provider: Provider, accessKey: String, secretKey: String) extends CredentialsResource {
  override def toHCL: String =
    s"""provider "aws" {
       |  region     = "${provider.region}"
       |  access_key = "$accessKey"
       |  secret_key = "$secretKey"
       |}""".stripMargin
}