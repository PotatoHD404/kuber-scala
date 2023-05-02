package terraform

import terraform.resources.TerraformResource


object TerraformGenerator {
  def generateHCL(resources: List[TerraformResource]): String = {
    resources.map(_.toHCL).mkString("\n\n")
  }
}
