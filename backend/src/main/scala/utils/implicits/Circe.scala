package utils.implicits

import java.sql.Timestamp
import java.time.Duration
import java.util.UUID

import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor, Json}

object Circe {

  def createEncoder[A](fn: A => Json): Encoder[A] = (a: A) => fn(a)

  implicit val timeStampEncoder: Encoder[Timestamp] =
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
      .reduceOption(_ + " " + _)
      .getOrElse("0s")
  }

  implicit val uuidDecoder: Decoder[UUID] = new Decoder[UUID] {
    override def apply(c: HCursor): Result[UUID] =
      Decoder.decodeString.map(UUID.fromString).apply(c)
  }

  implicit val timeStampDecoder: Decoder[Timestamp] = new Decoder[Timestamp] {
    override def apply(c: HCursor): Result[Timestamp] =
      Decoder.decodeString.map(Timestamp.valueOf).apply(c)
  }

}
