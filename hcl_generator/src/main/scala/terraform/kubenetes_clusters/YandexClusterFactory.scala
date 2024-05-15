package terraform.kubenetes_clusters

import terraform.providers.yandex.datasources.yandex_compute_image.YandexComputeImage
import terraform.providers.yandex.resources.yandex_compute_disk.YandexComputeDisk
import terraform.providers.yandex.resources.yandex_compute_instance.{BootDisk, NetworkInterface, Resources, YandexComputeInstance}
import terraform.providers.yandex.resources.yandex_vpc_network.YandexVpcNetwork
import terraform.providers.yandex.resources.yandex_vpc_subnet.YandexVpcSubnet
import terraform.{BackendResource, InfrastructureResource, ProviderSettings}
import terraform.providers.yandex.{Yandex, YandexProviderConfig}
import terraform.providers.yandex.yandexprovidersettings.YandexProviderSettings

import java.util.Base64

case class VMConfig(
                     count: Int,
                     cores: Int,
                     memory: Int,
                     diskSize: Int,
                     sshKey: String
                   )

def intToBase16 (value: Int): String
= {
  f"$value%x"
}

case class YandexVMFactory(image: YandexComputeImage, subnet: YandexVpcSubnet, vmConfigs: List[VMConfig]) {
  def create: List[InfrastructureResource[Yandex]] = {
    vmConfigs.flatMap { config =>
      (1 to config.count).map { index =>
        val instanceName = s"instance_${intToBase16(config.hashCode())}_$index"
        val diskName = s"disk_${intToBase16(config.hashCode())}_$index"
        val disk = YandexComputeDisk(resourceName=diskName, size=Some(config.diskSize), `type`=Some("network-ssd"), zone=Some("ru-central1-a"), imageId=Some(image.id))
        val bootDisk = BootDisk(diskId = disk.id)
        val networkInterface = NetworkInterface(subnetId = subnet.id, nat = Some(true))
        val resources = Resources(cores = config.cores, memory = config.memory)
        val metadata = Map("ssh-keys" -> config.sshKey)

        YandexComputeInstance(
          resourceName = instanceName,
          bootDisk = bootDisk,
          networkInterface = List(networkInterface),
          resources = resources,
          metadata = Some(metadata),
          platformId = Some("standard-v1")
        ) :: disk :: Nil
      }
    }
  }.flatten
}

case class YandexClusterFactory[
  T1 <: ProviderSettings[Yandex],
  T2 <: BackendResource,
](provider: T1, backend: Option[T2] = None, vmConfigs: List[VMConfig]) {
  def create: YandexProviderConfig[T1, T2, InfrastructureResource[Yandex]] = {
    val image = YandexComputeImage("family_images_linux", family = Some("ubuntu-2004-lts"))
    val network = YandexVpcNetwork("foo")
    val subnet = YandexVpcSubnet("foo", networkId = network.id, v4CidrBlocks = "10.5.0.0/24" :: Nil)
    val vmFactory = YandexVMFactory(image, subnet, vmConfigs)
    val resources: List[InfrastructureResource[Yandex]] = image :: network :: subnet :: vmFactory.create
    YandexProviderConfig(provider, backend, resources)
  }
}