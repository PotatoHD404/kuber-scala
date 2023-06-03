package patched.skuber.operations

import skuber.{ObjectResource, Pod}

def processResources[T <: ObjectResource](resources: List[T], nodePods: List[Pod], deploymentNames: List[Option[String]] = List.empty): (List[T], List[Int]) = {
  resources.flatMap { resource =>
    if (deploymentNames.nonEmpty && deploymentNames.contains(resource.metadata.labels.get("app"))) {
      None
    } else {
      val increment = nodePods.count(pod => pod.metadata.labels.get("app") == resource.metadata.labels.get("app"))
      println(s"Found $increment pods for ${resource.kind} ${resource.metadata.name}")
      increment match {
        case 0 => None
        case _ => Some((increaseReplicas(resource, increment), increment))
      }
    }
  }.unzip
}