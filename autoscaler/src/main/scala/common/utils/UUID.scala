package common.utils

case class UUID(uuid: java.util.UUID) {
  override def toString: String = uuid.toString
}

object UUID {
  def generate(): UUID = UUID(java.util.UUID.randomUUID())

  def fromString(uuidString: String): UUID = UUID(java.util.UUID.fromString(uuidString))
}