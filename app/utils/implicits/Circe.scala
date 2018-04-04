package utils.implicits

import java.sql.Timestamp
import java.time.Duration

import io.circe.{Encoder, Json}

object Circe {
  implicit val timeStampEncoder: Encoder[java.sql.Timestamp] =
    new Encoder[java.sql.Timestamp] {
      override def apply(t: Timestamp): Json = Json.fromString(t.toString)
    }

  implicit val javaDurationEncoder: Encoder[Duration] =
    new Encoder[Duration] {
      override def apply(t: Duration): Json =
        Json.fromString(t.toString)
    }
}
