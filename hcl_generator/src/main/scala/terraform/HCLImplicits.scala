package terraform

object HCLImplicits {
  implicit class BooleanToHCL(value: Boolean) {
    def toHCL: String = value.toString
  }

  implicit class IntToHCL(value: Int) {
    def toHCL: String = value.toString
  }

  implicit class DoubleToHCL(value: Double) {
    def toHCL: String = value.toString
  }

  implicit class StringToHCL(value: String) {
    def toHCL: String = "\"" + value + "\""
  }

  implicit class ListToHCL[T](value: List[T]) {
    def toHCL: String = value.map(_.toString).mkString("[", ", ", "]") // adjust this based on your needs
  }

  implicit class MapToHCL[T](value: Map[String, String]) {
    def toHCL: String = value.map { case (k, v) => s"$k = ${v.toHCL}" }.mkString("\n") // adjust this based on your needs
  }

  implicit class SetToHCL[T](value: Set[T]) {
    def toHCL: String = value.map(_.toString).mkString("{", ", ", "}") // adjust this based on your needs
  }
}