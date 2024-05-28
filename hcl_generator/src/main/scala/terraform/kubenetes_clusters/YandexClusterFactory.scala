package terraform.kubenetes_clusters

import io.circe.{Decoder, HCursor}
import io.circe.jawn.decode
import terraform.providers.yandex.datasources.yandex_compute_image.YandexComputeImage
import terraform.providers.yandex.resources.yandex_compute_disk.YandexComputeDisk
import terraform.providers.yandex.resources.yandex_compute_instance.{BootDisk, NetworkInterface, Resources, YandexComputeInstance}
import terraform.providers.yandex.resources.yandex_vpc_address.{ExternalIpv4Address, YandexVpcAddress}
import terraform.providers.yandex.resources.yandex_vpc_network.YandexVpcNetwork
import terraform.providers.yandex.resources.yandex_vpc_security_group.{Egress, Ingress, YandexVpcSecurityGroup}
import terraform.providers.yandex.resources.yandex_vpc_subnet.YandexVpcSubnet
import terraform.{BackendResource, InfrastructureResource, ProviderSettings, State, UnquotedString}
import terraform.providers.yandex.{Yandex, YandexProviderConfig}

import java.io.PrintWriter
import scala.io.Source
import scala.util.{Failure, Success, Try, Using}
import sys.process.stringToProcess
import terraform.Decoders.stateDecoder

case class VMConfig(
                     count: Int,
                     cores: Int,
                     memory: Int,
                     diskSize: Int,
                   )

def intToBase16(value: Int): String = {
  f"$value%x"
}

case class CloudConfig(
                        sshPwauth: Boolean = false,
                        users: List[UserConfig],
                        runcmd: Option[List[String]] = None
                      ) {
  def toHCL: UnquotedString = {
    val sb = new StringBuilder
    sb.append("<<-EOT\n")
    sb.append("#cloud-config\n\n")
    sb.append(s"ssh_pwauth: ${if (sshPwauth) "yes" else "no"}\n\n")
    sb.append("users:\n")
    users.foreach { user =>
      sb.append(s"  - name: ${user.name}\n")
      sb.append(s"    gecos: ${user.gecos}\n")
      sb.append(s"    groups: ${user.groups.mkString(", ")}\n")
      sb.append(s"    shell: ${user.shell}\n")
      sb.append(s"    sudo: ${user.sudo}\n")
      sb.append("    ssh_authorized_keys:\n")
      user.sshAuthorizedKeys.foreach { key =>
        sb.append(s"      - $key\n")
      }
      sb.append("\n")
    }
    runcmd.foreach { cmds =>
      sb.append("runcmd:\n")
      cmds.foreach { cmd =>
        sb.append(s"  - $cmd\n")
      }
    }
    sb.append("EOT")
    UnquotedString(sb.toString())
  }
}

case class UserConfig(
                       name: String,
                       gecos: String,
                       groups: List[String],
                       shell: String,
                       sudo: String,
                       sshAuthorizedKeys: List[String]
                     )


case class YandexVMFactory(image: YandexComputeImage, subnet: YandexVpcSubnet, securityGroup: YandexVpcSecurityGroup, vmConfigs: List[VMConfig]) {
  def create(k3sToken: String): List[InfrastructureResource[Yandex]] = {
    val sshKey = {
      val source = Source.fromFile("id_rsa.pub")
      try {
        source.getLines().mkString
      } finally {
        source.close()
      }
    }
    vmConfigs.zipWithIndex.flatMap { case (config, configIndex) =>
      val masterInstanceName = s"master-${configIndex + 1}"
      (1 to config.count).map { instanceIndex =>
        val instanceName = if (instanceIndex == 1) {
          masterInstanceName
        } else {
          s"slave-${configIndex + 1}-$instanceIndex"
        }
        val diskName = s"disk-${intToBase16(config.hashCode())}-${configIndex + 1}-$instanceIndex"
        val disk = YandexComputeDisk(resourceName = diskName, size = Some(config.diskSize), `type` = Some("network-ssd"), zone = Some("ru-central1-a"), imageId = Some(image.id), name = Some(instanceName), description = Some(s"Диск для $instanceName"))
        val bootDisk = BootDisk(diskId = disk.id)

        val vpcAddress = YandexVpcAddress(
          resourceName = s"address-$instanceName",
          name = Some(instanceName),
          description = Some(s"Адрес для $instanceName"),
          externalIpv4Address = Some(List(ExternalIpv4Address(zoneId = Some("ru-central1-a"))))
        )

        val networkInterface = NetworkInterface(subnetId = subnet.id, securityGroupIds = Some(Set(securityGroup.id)), nat = Some(true), natIpAddress = Some(UnquotedString(s"yandex_vpc_address.${vpcAddress.resourceName}.external_ipv4_address[0].address")))
        val resources = Resources(cores = config.cores, memory = config.memory)

        val cloudConfig = if (instanceIndex == 1) {
          val envFileContents = {
            val source = Source.fromFile(".env")
            try {
              source.getLines().mkString("\\n")
            } finally {
              source.close()
            }
          }

          CloudConfig(
            users = List(
              UserConfig(
                name = "ubuntu",
                gecos = "ubuntu",
                groups = List("sudo"),
                shell = "/bin/bash",
                sudo = "ALL=(ALL) NOPASSWD:ALL",
                sshAuthorizedKeys = List(sshKey)
              )
            ),
            runcmd = Some(List(
              s"""echo "${envFileContents.replace("$", "$$").replace("\"", """\"""")}" | sudo tee /home/ubuntu/.env""",
              s"""echo "${sshKey.replace("\n", "\\n").replace("$", "$$").replace("\"", """\"""")}" | sudo tee /home/ubuntu/id_rsa.pub""",
              s"curl -sfL https://get.k3s.io | sh -s - server --token $k3sToken",
              "sudo chown ubuntu:ubuntu /etc/rancher/k3s/k3s.yaml",
              "sudo chmod -R u+r /etc/rancher/k3s/k3s.yaml",
              "echo 'export KUBECONFIG=/etc/rancher/k3s/k3s.yaml' >> /home/ubuntu/.bashrc",
              "curl -fsSL -o get_helm.sh https://raw.githubusercontent.com/helm/helm/master/scripts/get-helm-3",
              "chmod 700 get_helm.sh",
              "bash ./get_helm.sh",
              "helm --kubeconfig /etc/rancher/k3s/k3s.yaml repo add autoscaler https://potatohd404.github.io/autoscaler-helm/",
              "helm --kubeconfig /etc/rancher/k3s/k3s.yaml repo update",
              "helm --kubeconfig /etc/rancher/k3s/k3s.yaml install my-autoscaler autoscaler/autoscaler"))
          )
        } else {
          CloudConfig(
            users = List(
              UserConfig(
                name = "ubuntu",
                gecos = "ubuntu",
                groups = List("sudo"),
                shell = "/bin/bash",
                sudo = "ALL=(ALL) NOPASSWD:ALL",
                sshAuthorizedKeys = List(sshKey)
              )
            ),
            runcmd = Some(List(s"curl -sfL https://get.k3s.io | K3S_URL=https://$${yandex_compute_instance.$masterInstanceName.network_interface.0.nat_ip_address}:6443 K3S_TOKEN=$k3sToken sh -"))
          )
        }

        val metadata = Map("user-data" -> cloudConfig.toHCL)

        YandexComputeInstance(
          resourceName = instanceName,
          bootDisk = bootDisk,
          networkInterface = List(networkInterface),
          resources = resources,
          metadata = Some(metadata),
          platformId = Some("standard-v3"),
          name = Some(instanceName),
          description = Some(s"VM $instanceName")
        ) :: disk :: vpcAddress :: Nil
      }
    }.flatten
  }
}

trait Cluster {
  def upscale(n: Int): Unit

  def downscale(n: Int): Unit

  def applyTerraformConfig(terraformFilePath: String = "cluster.tf"): Unit
}

case class Instance(name: String, externalIp: String, internalIp: String)

case class YandexCluster[
  T1 <: ProviderSettings[Yandex],
  T2 <: BackendResource,
](provider: T1, backend: Option[T2] = None, k3sToken: String, var vmConfigs: List[VMConfig]) extends Cluster {

  var instances: List[Instance] = List.empty

  def create: YandexProviderConfig[T1, T2, InfrastructureResource[Yandex]] = {
    val image = YandexComputeImage("family-images-linux", family = Some("ubuntu-2004-lts"))
    val network = YandexVpcNetwork("my-vpc-network", name = Some("my-vpc-network"), description = Some("My VPC network"))
    val subnet = YandexVpcSubnet("my-subnet", networkId = network.id, v4CidrBlocks = "10.5.0.0/24" :: Nil, name = Some("my-subnet"), description = Some("Моя подсеть"))
    val securityGroup = YandexVpcSecurityGroup(
      "k3s-security-group",
      name = Some("k3s-security-group"),
      description = Some("Группа безопасности для k3s кластера"),
      networkId = network.id,
      ingress = Some(Set(
        Ingress(protocol = "TCP", port = Some(6443), v4CidrBlocks = "0.0.0.0/0" :: Nil),
        Ingress(protocol = "TCP", port = Some(10250), v4CidrBlocks = "0.0.0.0/0" :: Nil),
        Ingress(protocol = "TCP", fromPort = Some(2379), toPort = Some(2380), v4CidrBlocks = "0.0.0.0/0" :: Nil),
        Ingress(protocol = "TCP", fromPort = Some(22), toPort = Some(22), v4CidrBlocks = "0.0.0.0/0" :: Nil),
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

    readTerraformState()
    printInstances()
  }

  implicit val anyDecoder: Decoder[Any] = (c: HCursor) => Try(c.value.toString).toEither.left.map(err =>
    io.circe.DecodingFailure(err.getMessage, c.history)
  )

  implicit val mapDecoder: Decoder[Map[String, Any]] = Decoder.decodeMap[String, Any]

  def readTerraformState(): Unit = {
    val tfstateJson = "terraform state pull".!!

    val result = decode[State](tfstateJson)
    result match {
      case Right(state) => // Успешный парсинг
        instances = state.resources.flatMap { resource =>
          resource.instances.flatMap { instance =>
            instance.attributes.network_interface.map { interfaces =>
              interfaces.map { interface =>
                Instance(resource.name, interface.nat_ip_address, interface.ip_address)
              }
            }
          }
        }.flatten
      case Left(error) => // Ошибка парсинга
        println(s"Ошибка парсинга JSON: ${error.getMessage}")
    }
  }

  def printInstances(): Unit = {
    println("Созданные экземпляры:")
    instances.foreach { instance =>
      println(s"Имя: ${instance.name}")
      println(s"Внешний IP-адрес: ${instance.externalIp}")
      println(s"Внутренний IP-адрес: ${instance.internalIp}")
      println("------------------------")
    }
  }
}