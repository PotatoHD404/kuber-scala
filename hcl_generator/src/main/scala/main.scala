import terraform.kubenetes_clusters.{Cluster, VMConfig, YandexCluster}
import terraform.providers.yandex.yandexprovidersettings.YandexProviderSettings

import java.io.PrintWriter
import scala.io.Source
import scala.sys.process.*
import scala.util.{Failure, Success, Try, Using}

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

  val cluster = YandexCluster(provider, vmConfigs = vmConfigs, k3sToken = k3sToken)
  cluster.applyTerraformConfig(terraformFilePath)

  // Увеличить количество виртуальных машин на 1
//  cluster.upscale(1)
//  cluster.applyTerraformConfig(terraformFilePath)

  // Уменьшить количество виртуальных машин на 1
//  cluster.downscale(1)
//  cluster.applyTerraformConfig(terraformFilePath)
}