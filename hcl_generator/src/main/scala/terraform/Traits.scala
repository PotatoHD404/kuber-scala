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

//trait TerraformResourceHCL[T <: InfrastructureResource[_]] {
//  def toHCL(resource: T): String
//}

//object TerraformResourceHCL {
//  def apply[T <: InfrastructureResource[_]](implicit hcl: TerraformResourceHCL[T]): TerraformResourceHCL[T] = hcl
//}
//
//case class Network[T <: ProviderType](name: String, cidr: String)(implicit hcl: TerraformResourceHCL[Network[T]]) extends InfrastructureResource[T] {
//  def toHCL: String = hcl.toHCL(this)
//}
//
//case class VM[T <: ProviderType](name: String, network: Network[T])(implicit hcl: TerraformResourceHCL[VM[T]]) extends InfrastructureResource[T] {
//  def toHCL: String = hcl.toHCL(this)
//}

case class ProviderConfig[
  A <: ProviderType,
  T1 <: ProviderSettings[A],
  T2 <: BackendResource,
  T3 <: InfrastructureResource[A]
]
(provider: T1, backend: Option[T2], resources: List[T3], providerName: Option[String] = None, source: Option[String] = None, version: Option[String] = None, inputVars: List[InputVarResource] = List(), localVars: List[LocalVarResource] = List(), outputVars: List[OutputVarResource] = List()) extends TerraformResource {
  def toHCL: String = {
    val allResources = provider +: backend.toList ++: resources ++: inputVars ++: localVars ++: outputVars
    s"""
       |terraform {
       |  required_providers {
       |    ${providerName.getOrElse("")} = {
       |      source = "${source.getOrElse("")}"
       |    }
       |  }
       |  required_version = "${version.getOrElse("")}"
       |}
       |
       |${allResources.map(_.toHCL).mkString("\n\n")}
       |""".stripMargin
  }
}