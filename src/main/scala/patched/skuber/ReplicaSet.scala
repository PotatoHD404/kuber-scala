package patched.skuber

import play.api.libs.json.{Format, JsPath}
import skuber.{ResourceDefinition, ResourceSpecification}
import skuber.apps.v1.ReplicaSet.{Spec, Status}
import skuber.apps.v1.{ReplicaSet, ReplicaSetList}
import skuber.json.format.*

implicit lazy val depFormat: Format[ReplicaSet] = (objFormat and
  (JsPath \ "spec").formatNullable[Spec] and
  (JsPath \ "status").formatNullable[Status])(ReplicaSet.apply, dp => (dp.kind, dp.apiVersion, dp.metadata, dp.spec, dp.status))

implicit val deployListFormat: Format[ReplicaSetList] = ListResourceFormat[ReplicaSet]

implicit val deployDef: ResourceDefinition[ReplicaSet] = new ResourceDefinition[ReplicaSet] {
  def spec: ResourceSpecification = ReplicaSet.specification
}
implicit val deployListDef: ResourceDefinition[ReplicaSetList] = new ResourceDefinition[ReplicaSetList] {
  def spec: ResourceSpecification = ReplicaSet.specification
}