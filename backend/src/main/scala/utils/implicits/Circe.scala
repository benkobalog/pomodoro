package utils.implicits

import java.time.Duration

import io.circe.{Encoder, Json}

object Circe {

  def createEncoder[A](fn: A => Json): Encoder[A] = (a: A) => fn(a)

  implicit val timeStampEncoder: Encoder[java.sql.Timestamp] =
    createEncoder(
      timestamp => Json.fromString(timestamp.toString.takeWhile(_ != '.')))

  implicit val javaDurationEncoder: Encoder[Duration] =
    createEncoder { durationFormatter _ andThen Json.fromString }

  def durationFormatter(duration: Duration): String = {
    val allSeconds = duration.getSeconds
    val hours = allSeconds / 3600
    val minutes = (allSeconds - hours * 3600) / 60
    val seconds = allSeconds - hours * 3600 - minutes * 60
    List((hours, "h"), (minutes, "m"), (seconds, "s"))
      .collect { case (amount, unit) if amount != 0 => amount + unit }
      .reduce(_ + " " + _)
  }
}
