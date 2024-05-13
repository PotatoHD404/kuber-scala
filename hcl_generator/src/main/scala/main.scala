import terraform.kubenetes_clusters.{VMConfig, YandexClusterFactory}
import terraform.providers.yandex.yandexprovidersettings.YandexProviderSettings

import java.io.PrintWriter
import scala.io.Source
import scala.util.{Failure, Try, Using}

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

@main
def main(): Unit = {
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
      sshKey = "ubuntu:${file(\"./id_rsa.pub\")}"
    )
  )
  val terraformString = YandexClusterFactory(provider, vmConfigs=vmConfigs).create.toHCL
  println(terraformString)
  Using.resource(new PrintWriter("terraformOutput.tf")) { writer =>
    writer.write(terraformString)
  }
}
