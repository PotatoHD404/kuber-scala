package terraform

import terraform.Resources.{BackendResource, CredentialsResource, InfrastructureResource}


case class TerraformConfig(credentials: CredentialsResource, backend: Option[BackendResource], resources: List[InfrastructureResource]) {
  def toHCL: String = {
    val allResources = credentials :: backend.toList ++ resources
    allResources.map(_.toHCL).mkString("\n\n")
  }
}

object TerraformConfig {
  // TODO
//  def fromHCL(hclCode: String): TerraformConfig = {
//    val hcl = parseHcl(hclCode)
//
//    val providerConfigs = extractResourceConfigs[ProviderConfig](hcl, "provider")
//    val vmConfigs = extractResourceConfigs[VMConfig](hcl, "vm")
//    val networkConfigs = extractResourceConfigs[NetworkConfig](hcl, "network")
//    // ... (extract configs for the remaining custom classes)
//
//    val resources = toCustomClasses(providerConfigs, vmConfigs, networkConfigs)
//    new TerraformConfig(resources)
//  }
}
