package terraform.kubenetes_clusters

import terraform.providers.yandex.datasources.yandex_compute_image.YandexComputeImage
import terraform.providers.yandex.resources.yandex_vpc_network.YandexVpcNetwork
import terraform.providers.yandex.resources.yandex_vpc_subnet.YandexVpcSubnet
import terraform.{BackendResource, InfrastructureResource, ProviderSettings}
import terraform.providers.yandex.{Yandex, YandexProviderConfig}
import terraform.providers.yandex.yandexprovidersettings.YandexProviderSettings

case class YandexVMFactory(image: YandexComputeImage, subnet: YandexVpcSubnet) {
  def create: List[InfrastructureResource[Yandex]] = {
    List[InfrastructureResource[Yandex]]()
  }
}

case class YandexClusterFactory[
  T1 <: ProviderSettings[Yandex],
  T2 <: BackendResource,
](provider: T1, backend: Option[T2] = None) {
  def create: YandexProviderConfig[T1, T2, InfrastructureResource[Yandex]] = {
    val image = YandexComputeImage("family_images_linux", family = Some("ubuntu-2004-lts"))
    val network = YandexVpcNetwork("foo")
    val subnet = YandexVpcSubnet("foo", networkId = network.id, v4CidrBlocks = "10.5.0.0/24" :: Nil)
    val resources: List[InfrastructureResource[Yandex]] = image :: Nil
    YandexProviderConfig(provider, backend, resources)
  }
}
