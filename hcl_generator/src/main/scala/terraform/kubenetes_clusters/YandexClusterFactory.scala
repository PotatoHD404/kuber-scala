package terraform.kubenetes_clusters

import terraform.{BackendResource, InfrastructureResource, ProviderSettings}
import terraform.providers.yandex.{Yandex, YandexProviderConfig}
import terraform.providers.yandex.yandexprovidersettings.YandexProviderSettings

case class YandexClusterFactory[
  T1 <: ProviderSettings[Yandex],
  T2 <: BackendResource,
  T3 <: InfrastructureResource[Yandex]
] (provider: T1, backend: Option[T2] = None){
  def get: YandexProviderConfig[T1, T2, T3] = {
    val resources = List[T3]()
    YandexProviderConfig(provider, backend, resources)
  }
}
