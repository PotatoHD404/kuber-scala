package cluster.creator

import terraform.{DotenvLoader, S3Backend, createCluster, envOrError, envOrNone}
import terraform.kubenetes.clusters.{Cluster, VMConfig, YandexCluster}
import terraform.providers.yandex.yandexprovidersettings.YandexProviderSettings

import java.io.PrintWriter
import scala.io.Source
import scala.sys.process.*
import scala.util.{Failure, Success, Try, Using}


@main
def main(): Unit = {
  val cluster = createCluster()
  cluster.applyTerraformConfig()

  // Увеличить количество виртуальных машин на 1
  // cluster.upscale(1)
  // cluster.applyTerraformConfig()

  // Уменьшить количество виртуальных машин на 1
  // cluster.downscale(1)
  // cluster.applyTerraformConfig()
}