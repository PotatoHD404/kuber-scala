package terraform

trait ProviderType

abstract class TerraformResource {
  def toHCL: String
}

trait InfrastructureResource[T <: ProviderType] extends TerraformResource

trait ProviderSettings[T <: ProviderType] extends TerraformResource

trait BackendResource extends TerraformResource

trait InputVarResource extends TerraformResource

trait LocalVarResource extends TerraformResource

trait OutputVarResource extends TerraformResource

case class S3Backend(
                      bucketName: String,
                      stateFileKey: String,
                      region: String,
                      s3Endpoint: String,
                      skipRegionValidation: Boolean = true,
                      skipCredentialsValidation: Boolean = true,
                      skipRequestingAccountId: Boolean = true,
                      skipS3Checksum: Boolean = true
                    ) extends BackendResource {
  def toHCL: String =
    s"""backend "s3" {
       |  endpoints = {
       |    s3 = "$s3Endpoint"
       |  }
       |  bucket = "$bucketName"
       |  region = "$region"
       |  key = "$stateFileKey"
       |  skip_region_validation = $skipRegionValidation
       |  skip_credentials_validation = $skipCredentialsValidation
       |  skip_requesting_account_id = $skipRequestingAccountId
       |  skip_s3_checksum = $skipS3Checksum
       |}""".stripMargin
}


case class ProviderConfig[
  A <: ProviderType,
  T1 <: ProviderSettings[A],
  T2 <: BackendResource,
  T3 <: InfrastructureResource[A]
]
(provider: T1, backend: Option[T2], resources: List[T3], providerName: String, source: String, version: String, inputVars: List[InputVarResource] = List(), localVars: List[LocalVarResource] = List(), outputVars: List[OutputVarResource] = List()) extends TerraformResource {
  def toHCL: String = {
    val allResources = provider +: backend.toList ++: resources ++: inputVars ++: localVars ++: outputVars
    s"""
       |terraform {
       |  required_providers {
       |    $providerName = {
       |      source = "$source"
       |    }
       |  }
       |  required_version = "$version"
       |}
       |
       |${allResources.map(_.toHCL).mkString("\n\n")}
       |""".stripMargin
  }
}