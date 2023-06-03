package patched.skuber.operations

import cats.effect.IO

import java.time.Duration
import scala.concurrent.Future
import scala.util.Try

object Conversions {
  //  given fromFutureToIO[T]: Conversion[Future[T], IO[T]] with {
  //    def apply(future: Future[T]): IO[T] = IO.fromFuture(IO(future))
  //  }
  /*implicit*/ def fromFutureToIO[T](future: Future[T]): IO[T] = IO.fromFuture(IO(future))
  extension [T](future: Future[T])
    def toIO: IO[T] = IO.fromFuture(IO(future))
}

def toDuration(value: String): Option[Duration] = {
  if (value.isEmpty) None
  else {
    val days = value.split("d").headOption.map(_.toInt)
    val hours = value.split("h").headOption.map(_.toInt)
    val minutes = value.split("m").headOption.map(_.toInt)
    val seconds = value.split("s").headOption.map(_.toInt)

    val duration = Duration.ofDays(days.getOrElse(0)).plusHours(hours.getOrElse(0)).plusMinutes(minutes.getOrElse(0)).plusSeconds(seconds.getOrElse(0))
    Some(duration)
  }
}

def toOption(value: String): Option[String] = {
  if (value.isEmpty) None
  else Some(value)
}

def stringToFloat(value: String): Option[Float] = {
  if (value.endsWith("m")) {
    Try((value.dropRight(1).toFloat) / 1000).toOption
  } else {
    Try(value.toFloat).toOption
  }
}

def stringToBoolean(s: String): Boolean = {
  s match {
    case "True" => true
    case "False" => false
    case _ => throw new IllegalArgumentException("Invalid boolean value: " + s)
  }
}

def formatDuration(duration: Duration): String = {
  val days = duration.toDaysPart
  val hours = duration.toHoursPart
  val minutes = duration.toMinutesPart
  val seconds = duration.toSecondsPart

  s"${if (days > 0) days + "d" else ""}${if (hours > 0) hours + "h" else ""}${if (minutes > 0) minutes + "m" else ""}${if (seconds > 0) seconds + "s" else ""}".trim
}
