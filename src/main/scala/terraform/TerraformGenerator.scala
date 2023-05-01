package terraform

import terraform.Resources.TerraformResource


object TerraformGenerator {
  def generateHCL(resources: List[TerraformResource]): String = {
    resources.map(_.toHCL).mkString("\n\n")
  }
}
