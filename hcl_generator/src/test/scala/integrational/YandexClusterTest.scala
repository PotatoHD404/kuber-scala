import com.github.javafaker.Faker
import org.scalatest.funsuite.AnyFunSuite
import terraform.kubenetes.clusters.{VMConfig, YandexCluster}
import terraform.{BackendResource, InfrastructureResource, ProviderSettings}
import terraform.providers.yandex.Yandex

import scala.reflect.ClassTag
import scala.util.{Failure, Random, Success, Try}
import sys.process.stringToProcess
import org.scalactic.Prettifier.default

class YandexClusterTest extends AnyFunSuite {

  def getRandomSubtype[T: ClassTag]: Class[_] = {
    val baseClass = implicitly[ClassTag[T]].runtimeClass
    val subtypes = baseClass.getDeclaredClasses.toList
    if (subtypes.isEmpty) {
      throw new IllegalArgumentException(s"No subtypes found for ${baseClass.getSimpleName}")
    }
    subtypes(Random.nextInt(subtypes.length))
  }

  def createRandomInstance[T: ClassTag](implicit faker: Faker): T = {
    val clazz = getRandomSubtype[T]
    val constructor = clazz.getConstructors.head
    val params = constructor.getParameterTypes.map {
      case cls if cls == classOf[String] => faker.lorem().word()
      case cls if cls == classOf[Int] => faker.number().numberBetween(1, 100)
      case cls if cls == classOf[Boolean] => faker.bool().bool()
      case cls if cls == classOf[Double] => faker.number().randomDouble(2, 1, 100)
      case cls if cls == classOf[Long] => faker.number().numberBetween(1L, 100L)
      // Добавьте другие типы по необходимости
      case paramType => throw new IllegalArgumentException(s"Unsupported parameter type: $paramType")
    }
    constructor.newInstance(params: _*).asInstanceOf[T]
  }

  def generateTerraformConfig[T1 <: ProviderSettings[Yandex], T2 <: BackendResource, T3 <: InfrastructureResource[Yandex]](
                                                                                                                            implicit faker: Faker,
                                                                                                                            ct1: ClassTag[T1],
                                                                                                                            ct2: ClassTag[T2],
                                                                                                                            ct3: ClassTag[T3]
                                                                                                                          ): YandexCluster[T1, T2] = {
    val provider = createRandomInstance[T1]
    val backend = if (Random.nextBoolean()) Some(createRandomInstance[T2]) else None
    val vmConfigs = List.fill(Random.nextInt(5) + 1)(createRandomInstance[VMConfig])
    YandexCluster(provider, backend, "", vmConfigs)
  }

  test("Generate and apply Terraform configuration") {
    implicit val faker: Faker = new Faker()

    val cluster = generateTerraformConfig[ProviderSettings[Yandex], BackendResource, InfrastructureResource[Yandex]]
    cluster.applyTerraformConfig()

    val fmtResult = Try {
      val fmtCommand = s"terraform fmt cluster.tf"
      val fmtExitCode = fmtCommand.!
      if (fmtExitCode != 0) {
        throw new RuntimeException(s"Command '$fmtCommand' exited with code $fmtExitCode")
      }
    }

    fmtResult match {
      case Success(_) => println("Terraform configuration formatted successfully.")
      case Failure(ex) => fail(s"Error formatting Terraform configuration: ${ex.getMessage}")
    }
  }
}