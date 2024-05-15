import terraform.kubenetes_clusters.{VMConfig, YandexClusterFactory}
import terraform.providers.yandex.yandexprovidersettings.YandexProviderSettings

import java.io.PrintWriter
import scala.io.Source
import scala.sys.process._
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

  val terraformFilePath = "terraformOutput.tf"

  val terraformString = YandexClusterFactory(provider, vmConfigs=vmConfigs).create.toHCL
//  println(terraformString)
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

  // Обработка результата выполнения команды terraform fmt
  fmtResult match {
    case Success(_) =>
      println(s"Terraform configuration in $terraformFilePath formatted successfully.")
    case Failure(ex) =>
      println(s"Error formatting Terraform configuration in $terraformFilePath: ${ex.getMessage}")
  }
}
