package terraform.kubenetes_clusters

import terraform.providers.yandex.datasources.yandex_compute_image.YandexComputeImage
import terraform.providers.yandex.resources.yandex_compute_disk.YandexComputeDisk
import terraform.providers.yandex.resources.yandex_compute_instance.{BootDisk, NetworkInterface, Resources, YandexComputeInstance}
import terraform.providers.yandex.resources.yandex_vpc_network.YandexVpcNetwork
import terraform.providers.yandex.resources.yandex_vpc_security_group.{Egress, Ingress, YandexVpcSecurityGroup}
import terraform.providers.yandex.resources.yandex_vpc_subnet.YandexVpcSubnet
import terraform.{BackendResource, InfrastructureResource, ProviderSettings, UnquotedString}
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


case class YandexVMFactory(image: YandexComputeImage, subnet: YandexVpcSubnet, securityGroup: YandexVpcSecurityGroup, vmConfigs: List[VMConfig]) {
  def create(k3sToken: String): List[InfrastructureResource[Yandex]] = {
    vmConfigs.zipWithIndex.flatMap { case (config, configIndex) =>
      val masterInstanceName = s"master_${configIndex + 1}"
      (1 to config.count).map { instanceIndex =>
        val instanceName = if (instanceIndex == 1) {
          masterInstanceName
        } else {
          s"slave_${configIndex + 1}_$instanceIndex"
        }
        val diskName = s"disk_${intToBase16(config.hashCode())}_${configIndex + 1}_$instanceIndex"
        val disk = YandexComputeDisk(resourceName = diskName, size = Some(config.diskSize), `type` = Some("network-ssd"), zone = Some("ru-central1-a"), imageId = Some(image.id))
        val bootDisk = BootDisk(diskId = disk.id)
        val networkInterface = NetworkInterface(subnetId = subnet.id, securityGroupIds = Some(Set(securityGroup.id)))
        val resources = Resources(cores = config.cores, memory = config.memory)
        val metadata: Map[String, UnquotedString] = if (instanceIndex == 1) {
          // Master node
          Map(
            "ssh-keys" -> config.sshKey,
            "user-data" ->
              UnquotedString(s"""<<-EOT
                                |#cloud-config
                                |runcmd:
                                |  - curl -sfL https://get.k3s.io | sh -
                                |EOT""".stripMargin)
          )
        } else {
          // Slave nodes
          Map(
            "ssh-keys" -> config.sshKey,
            "user-data" ->
              UnquotedString(s"""<<-EOT
                                |#cloud-config
                                |runcmd:
                                |  - curl -sfL https://get.k3s.io | K3S_URL=https://$${yandex_compute_instance.$masterInstanceName.network_interface.0.nat_ip_address}:6443 K3S_TOKEN=$k3sToken sh -
                                |EOT""".stripMargin)
          )
        }

        YandexComputeInstance(
          resourceName = instanceName,
          bootDisk = bootDisk,
          networkInterface = List(networkInterface),
          resources = resources,
          metadata = Some(metadata),
          platformId = Some("standard-v1")
        ) :: disk :: Nil
      }
    }.flatten
  }
}

trait Cluster {
  def upscale(n: Int): Unit
  def downscale(n: Int): Unit
  def applyTerraformConfig(terraformFilePath: String = "cluster.tf"): Unit
}

case class YandexCluster[
  T1 <: ProviderSettings[Yandex],
  T2 <: BackendResource,
](provider: T1, backend: Option[T2] = None, k3sToken: String, var vmConfigs: List[VMConfig]) extends Cluster {

  def create: YandexProviderConfig[T1, T2, InfrastructureResource[Yandex]] = {
    val image = YandexComputeImage("family_images_linux", family = Some("ubuntu-2004-lts"))
    val network = YandexVpcNetwork("my_vpc_network")
    val subnet = YandexVpcSubnet("my_subnet", networkId = network.id, v4CidrBlocks = "10.5.0.0/24" :: Nil)
    val securityGroup = YandexVpcSecurityGroup(
      "k3s_security_group",
      name = Some("k3s-security-group"),
      description = Some("Security group for k3s cluster"),
      networkId = network.id,
      ingress = Some(Set(
        Ingress(protocol = "TCP", port = Some(6443), v4CidrBlocks = "0.0.0.0/0" :: Nil),
        Ingress(protocol = "TCP", port = Some(10250), v4CidrBlocks = "0.0.0.0/0" :: Nil),
        Ingress(protocol = "TCP", fromPort = Some(2379), toPort = Some(2380), v4CidrBlocks = "0.0.0.0/0" :: Nil),
        Ingress(protocol = "UDP", fromPort = Some(8472), toPort = Some(8472), v4CidrBlocks = "0.0.0.0/0" :: Nil)
      )),
      egress = Some(Set(
        Egress(protocol = "ANY", fromPort = Some(0), toPort = Some(65535), v4CidrBlocks = "0.0.0.0/0" :: Nil)
      ))
    )
    val vmFactory = YandexVMFactory(image, subnet, securityGroup, vmConfigs)
    val resources: List[InfrastructureResource[Yandex]] = image :: network :: subnet :: securityGroup :: vmFactory.create(k3sToken)
    YandexProviderConfig(provider, backend, resources)
  }

  override def upscale(n: Int): Unit = {
    require(n > 0, "Number of instances to add must be positive")
    val updatedConfigs = vmConfigs match {
      case head :: tail => head.copy(count = head.count + n) :: tail
      case _ => vmConfigs
    }
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

    def runCommand(command: String, successMessage: String, errorMessage: String): Unit = {
      val result = Try {
        val exitCode = command.!
        if (exitCode != 0) {
          throw new RuntimeException(s"Command '$command' exited with code $exitCode")
        }
      }

      result match {
        case Success(_) =>
          println(successMessage)
        case Failure(ex) =>
          throw Error(s"$errorMessage: ${ex.getMessage}")
      }
    }

    runCommand(
      command = s"terraform fmt $terraformFilePath",
      successMessage = s"Terraform configuration in $terraformFilePath formatted successfully.",
      errorMessage = s"Error formatting Terraform configuration in $terraformFilePath"
    )

    runCommand(
      command = "terraform init",
      successMessage = "Terraform initialized successfully.",
      errorMessage = "Error initializing Terraform"
    )

    runCommand(
      command = "terraform apply -auto-approve",
      successMessage = "Terraform apply completed successfully.",
      errorMessage = "Error applying Terraform configuration"
    )
  }
}