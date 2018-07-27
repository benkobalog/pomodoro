package utils.implicits

import java.time.Duration

import io.circe.{Encoder, Json}

object Circe {

  def createEncoder[A](fn: A => Json): Encoder[A] = (a: A) => fn(a)

  implicit val timeStampEncoder: Encoder[java.sql.Timestamp] =
    createEncoder(
      timestamp => Json.fromString(timestamp.toString.takeWhile(_ != '.')))

  implicit val javaDurationEncoder: Encoder[Duration] =
    createEncoder { duration =>
      val string =
        duration.toString.replaceAll("(H|M|S)", "$1 ").toLowerCase.trim
      Json.fromString(string)
    }
}
