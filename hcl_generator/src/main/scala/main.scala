import terraform.{DotenvLoader, S3Backend, envOrError, envOrNone}
import terraform.kubenetes_clusters.{Cluster, VMConfig, YandexCluster}
import terraform.providers.yandex.yandexprovidersettings.YandexProviderSettings

import java.io.PrintWriter
import scala.io.Source
import scala.sys.process.*
import scala.util.{Failure, Success, Try, Using}


@main
def main(): Unit = {
  DotenvLoader.load()
  val provider = YandexProviderSettings(
    cloudId = envOrNone("YC_CLOUD_ID"),
    folderId = envOrNone("YC_FOLDER_ID"),
    token = envOrNone("YC_TOKEN"),
    zone = envOrNone("YC_ZONE")
  )
//  val backend = Backend
  val vmConfigs = List(
    VMConfig(
      count = 2,
      cores = 2,
      memory = 4,
      diskSize = 20,
    )
  )

  val terraformFilePath = "terraformOutput.tf"
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

  val cluster = YandexCluster(provider, backendConfig, vmConfigs = vmConfigs, k3sToken = k3sToken)
  cluster.applyTerraformConfig(terraformFilePath)

  // Увеличить количество виртуальных машин на 1
//  cluster.upscale(1)
//  cluster.applyTerraformConfig(terraformFilePath)

  // Уменьшить количество виртуальных машин на 1
//  cluster.downscale(1)
//  cluster.applyTerraformConfig(terraformFilePath)
}