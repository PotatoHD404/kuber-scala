package terraform.kubenetes_clusters

import terraform.providers.yandex.datasources.yandex_compute_image.YandexComputeImage
import terraform.providers.yandex.resources.yandex_compute_disk.YandexComputeDisk
import terraform.providers.yandex.resources.yandex_compute_instance.{BootDisk, NetworkInterface, Resources, YandexComputeInstance}
import terraform.providers.yandex.resources.yandex_vpc_network.YandexVpcNetwork
import terraform.providers.yandex.resources.yandex_vpc_subnet.YandexVpcSubnet
import terraform.{BackendResource, InfrastructureResource, ProviderSettings}
import terraform.providers.yandex.{Yandex, YandexProviderConfig}

import java.io.PrintWriter
import scala.util.{Failure, Success, Try, Using}
import sys.process.stringToProcess

case class VMConfig(
                     count: Int,
                     cores: Int,
                     memory: Int,
                     diskSize: Int,
                     sshKey: String
                   )

def intToBase16(value: Int): String = {
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

trait Cluster {
  def upscale(n: Int): Unit
  def downscale(n: Int): Unit
  def applyTerraformConfig(terraformFilePath: String = "cluster.tf"): Unit
}

case class YandexCluster[
  T1 <: ProviderSettings[Yandex],
  T2 <: BackendResource,
](provider: T1, backend: Option[T2] = None, var vmConfigs: List[VMConfig]) extends Cluster {

  def create: YandexProviderConfig[T1, T2, InfrastructureResource[Yandex]] = {
    val image = YandexComputeImage("family_images_linux", family = Some("ubuntu-2004-lts"))
    val network = YandexVpcNetwork("foo")
    val subnet = YandexVpcSubnet("foo", networkId = network.id, v4CidrBlocks = "10.5.0.0/24" :: Nil)
    val vmFactory = YandexVMFactory(image, subnet, vmConfigs)
    val resources: List[InfrastructureResource[Yandex]] = image :: network :: subnet :: vmFactory.create
    YandexProviderConfig(provider, backend, resources)
  }

  override def upscale(n: Int): Unit = {
    require(n > 0, "Number of instances to add must be positive")
    val updatedConfigs = vmConfigs match {
      case head :: tail => head.copy(count = head.count + n) :: tail
      case _ => vmConfigs
    }
    require(vmConfigs.head.count > 0, "Number of instances to add must be positive")
    vmConfigs = updatedConfigs
  }

  override def downscale(n: Int): Unit = {
    require(n > 0, "Number of instances to remove must be positive")
    val updatedConfigs = vmConfigs match {
      case head :: tail => head.copy(count = Math.max(head.count - n, 0)) :: tail
      case _ => vmConfigs
    }
    vmConfigs = updatedConfigs
  }

  def applyTerraformConfig(terraformFilePath: String = "cluster.tf"): Unit = {
    val terraformString = create.toHCL
    Using.resource(new PrintWriter(terraformFilePath)) { writer =>
      writer.write(terraformString)
    }

    val fmtResult = Try {
      val fmtCommand = s"terraform fmt $terraformFilePath"
      val fmtExitCode = fmtCommand.!
      if (fmtExitCode != 0) {
        throw new RuntimeException(s"Command '$fmtCommand' exited with code $fmtExitCode")
      }
    }

    fmtResult match {
      case Success(_) =>
        println(s"Terraform configuration in $terraformFilePath formatted successfully.")
      case Failure(ex) =>
        println(s"Error formatting Terraform configuration in $terraformFilePath: ${ex.getMessage}")
    }

    val initResult = Try {
      val initCommand = s"terraform init"
      val initExitCode = initCommand.!
      if (initExitCode != 0) {
        throw new RuntimeException(s"Command '$initCommand' exited with code $initExitCode")
      }
    }

    initResult match {
      case Success(_) =>
        println("Terraform initialized successfully.")
      case Failure(ex) =>
        println(s"Error initializing Terraform: ${ex.getMessage}")
    }

    val applyResult = Try {
      val applyCommand = s"terraform apply -auto-approve"
      val applyExitCode = applyCommand.!
      if (applyExitCode != 0) {
        throw new RuntimeException(s"Command '$applyCommand' exited with code $applyExitCode")
      }
    }

    applyResult match {
      case Success(_) =>
        println("Terraform apply completed successfully.")
      case Failure(ex) =>
        println(s"Error applying Terraform configuration: ${ex.getMessage}")
    }
  }
}