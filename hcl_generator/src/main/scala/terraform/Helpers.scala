package terraform

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import terraform.kubenetes.clusters.{VMConfig, YandexCluster}
import terraform.providers.yandex.Yandex
import terraform.providers.yandex.yandexprovidersettings.YandexProviderSettings

import scala.io.Source
import scala.util.{Failure, Try, Using}
import scala.util.matching.Regex

case class Region(value: String)

case class CIDRBlock(value: String) {
  private val cidrPattern: Regex = """^(\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}\/\d{1,2})$""".r
  require(cidrPattern.matches(value), s"Invalid CIDR block: $value")
}

case class PortRange(from: Int, to: Int) {
  require(from >= 0 && from <= 65535, s"Invalid from port: $from")
  require(to >= 0 && to <= 65535, s"Invalid to port: $to")
  require(from <= to, s"From port ($from) must be less than or equal to the to port ($to)")
}

case class Image(publisher: String, offer: String, sku: String)

case class State(version: Int,
                 terraform_version: String,
                 serial: Int,
                 lineage: String,
                 outputs: Map[String, Output],
                 resources: List[Resource])

case class Output(value: String, `type`: String)

case class Resource(mode: String,
                    `type`: String,
                    name: String,
                    provider: String,
                    instances: List[Instance])

case class Instance(schema_version: Int,
                    attributes: Attributes,
                    sensitive_attributes: List[String],
                    `private`: Option[String],
                    dependencies: Option[List[String]])

case class Attributes(allow_recreate: Option[String],
                      allow_stopping_for_update: Option[String],
                      boot_disk: Option[List[BootDisk]],
                      created_at: Option[String],
                      deletion_protection: Option[Boolean],
                      description: Option[String],
                      dns_record: Option[List[String]],
                      external_ipv4_address: Option[List[ExternalIPv4Address]],
                      folder_id: Option[String],
                      id: Option[String],
                      labels: Option[Map[String, String]],
                      metadata: Option[Map[String, String]],
                      metadata_options: Option[List[MetadataOptions]],
                      name: Option[String],
                      network_acceleration_type: Option[String],
                      network_interface: Option[List[NetworkInterface]],
                      placement_policy: Option[List[PlacementPolicy]],
                      platform_id: Option[String],
                      resources: Option[List[Resources]],
                      scheduling_policy: Option[List[SchedulingPolicy]],
                      secondary_disk: Option[List[String]],
                      service_account_id: Option[String],
                      status: Option[String],
                      timeouts: Option[String],
                      zone: Option[String])

case class BootDisk(auto_delete: Boolean,
                    device_name: String,
                    disk_id: String,
                    initialize_params: List[InitializeParams],
                    mode: String)

case class InitializeParams(block_size: Option[Int],
                            description: Option[String],
                            image_id: Option[String],
                            name: Option[String],
                            size: Option[Int],
                            snapshot_id: Option[String],
                            `type`: Option[String])

case class ExternalIPv4Address(address: String,
                               ddos_protection_provider: String,
                               outgoing_smtp_capability: String,
                               zone_id: String)

case class MetadataOptions(aws_v1_http_endpoint: Int,
                           aws_v1_http_token: Int,
                           gce_http_endpoint: Int,
                           gce_http_token: Int)

case class NetworkInterface(dns_record: List[String],
                            index: Int,
                            ip_address: String,
                            ipv4: Boolean,
                            ipv6: Boolean,
                            ipv6_address: String,
                            ipv6_dns_record: List[String],
                            mac_address: String,
                            nat: Boolean,
                            nat_dns_record: List[String],
                            nat_ip_address: String,
                            nat_ip_version: String,
                            security_group_ids: List[String],
                            subnet_id: String)

case class PlacementPolicy(host_affinity_rules: List[String],
                           placement_group_id: String,
                           placement_group_partition: Int)

case class Resources(core_fraction: Int,
                     cores: Int,
                     gpus: Int,
                     memory: Int)

case class SchedulingPolicy(preemptible: Boolean)

object Decoders {
  implicit val outputDecoder: Decoder[Output] = deriveDecoder[Output]
  implicit val initializeParamsDecoder: Decoder[InitializeParams] = deriveDecoder[InitializeParams]
  implicit val bootDiskDecoder: Decoder[BootDisk] = deriveDecoder[BootDisk]
  implicit val externalIPv4AddressDecoder: Decoder[ExternalIPv4Address] = deriveDecoder[ExternalIPv4Address]
  implicit val metadataOptionsDecoder: Decoder[MetadataOptions] = deriveDecoder[MetadataOptions]
  implicit val networkInterfaceDecoder: Decoder[NetworkInterface] = deriveDecoder[NetworkInterface]
  implicit val placementPolicyDecoder: Decoder[PlacementPolicy] = deriveDecoder[PlacementPolicy]
  implicit val resourcesDecoder: Decoder[Resources] = deriveDecoder[Resources]
  implicit val schedulingPolicyDecoder: Decoder[SchedulingPolicy] = deriveDecoder[SchedulingPolicy]
  implicit val attributesDecoder: Decoder[Attributes] = deriveDecoder[Attributes]
  implicit val instanceDecoder: Decoder[Instance] = deriveDecoder[Instance]
  implicit val resourceDecoder: Decoder[Resource] = deriveDecoder[Resource]
  implicit val stateDecoder: Decoder[State] = deriveDecoder[State]
}

object DotenvLoader {
  def load(dotenvFilePath: String = ".env"): Unit = {
    Try(Using.resource(Source.fromFile(dotenvFilePath)) { source =>
      source.getLines().foreach { line =>
        val cleanLine = line.split("#").head.trim // Removes comments and trims
        if (cleanLine.nonEmpty) {
          val Array(key, value) = cleanLine.split("=", 2).map(_.trim)
          val processedValue = value.stripPrefix("\"").stripSuffix("\"") // Removes surrounding quotes if present
          System.setProperty(key, processedValue)
        }
      }
    }) match {
      case Failure(ex) =>
        println(s"Warning: Failed to load .env file from $dotenvFilePath. ${ex.getMessage}")
      case _ => // Success, do nothing
    }
  }
}

def envOrNone(name: String): Option[String] = {
  Option(System.getenv(name)).orElse(Option(System.getProperty(name)))
}

def envOrError(name: String): String = {
  Option(System.getenv(name))
    .orElse(Option(System.getProperty(name)))
    .getOrElse(throw new NoSuchElementException(s"No environment variable or system property found for '$name'"))
}

def createCluster(terraformFilePath: String = "cluster.tf"): YandexCluster[YandexProviderSettings, S3Backend] = {
  DotenvLoader.load()
  val provider = YandexProviderSettings(
    cloudId = envOrNone("YC_CLOUD_ID"),
    folderId = envOrNone("YC_FOLDER_ID"),
    token = envOrNone("YC_TOKEN"),
    zone = envOrNone("YC_ZONE")
  )

  val vmConfigs = List(
    VMConfig(
      count = 2,
      cores = 2,
      memory = 4,
      diskSize = 20,
    )
  )

  val k3sToken = envOrError("K3S_TOKEN")
  val backendConfig = Some(
    S3Backend(
      bucketName = "autoscaler-bucket",
      stateFileKey = "terraform.tfstate",
      region = "ru-central1",
      s3Endpoint = "https://storage.yandexcloud.net",
      accessKey = envOrError("S3_ACCESS_KEY"),
      secretKey = envOrError("S3_SECRET_KEY"),
    )
  )

  YandexCluster(provider, backendConfig, vmConfigs = vmConfigs, k3sToken = k3sToken)
}