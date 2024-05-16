import org.scalatest.funsuite.AnyFunSuite
import com.github.javafaker.Faker
import scala.reflect.runtime.universe._
import scala.util.{Success, Failure}
import scala.sys.process._

class YandexClusterTest extends AnyFunSuite {
  val faker = new Faker()

  // Функция для случайного создания экземпляра класса
  def createRandomInstance[T: TypeTag]: T = {
    val tpe = typeOf[T]
    val classSymbol = tpe.typeSymbol.asClass
    val constructor = tpe.decl(termNames.CONSTRUCTOR).asMethod
    val params = constructor.paramLists.head.map { param =>
      val paramType = param.typeSignature
      paramType match {
        case t if t <:< typeOf[String] => faker.lorem().word()
        case t if t <:< typeOf[Int] => faker.number().numberBetween(1, 10)
        case t if t <:< typeOf[Option[_]] =>
          val optionType = t.typeArgs.head
          if (faker.bool().bool()) Some(createRandomInstance(optionType)) else None
        case t if t <:< typeOf[List[_]] =>
          val listType = t.typeArgs.head
          List.fill(faker.number().numberBetween(1, 5))(createRandomInstance(listType))
        case _ => throw new IllegalArgumentException(s"Unsupported type: $paramType")
      }
    }
    val instanceMirror = runtimeMirror(getClass.getClassLoader).reflectClass(classSymbol)
    instanceMirror.reflectConstructor(constructor).apply(params: _*).asInstanceOf[T]
  }

  test("YandexCluster random test") {
    // Создаем случайный экземпляр YandexCluster
    val cluster = createRandomInstance[YandexCluster[ProviderSettings[Yandex], BackendResource]]

    // Вызываем метод applyTerraformConfig для генерации конфигурации
    cluster.applyTerraformConfig("test.tf")

    // Проверяем корректность сгенерированной конфигурации с помощью terraform fmt
    val fmtResult = Try {
      val fmtCommand = s"terraform fmt test.tf"
      val fmtExitCode = fmtCommand.!
      assert(fmtExitCode == 0, s"Command '$fmtCommand' exited with code $fmtExitCode")
    }

    fmtResult match {
      case Success(_) =>
        println("Terraform configuration formatted successfully.")
      case Failure(ex) =>
        fail(s"Error formatting Terraform configuration: ${ex.getMessage}")
    }
  }
}