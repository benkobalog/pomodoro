package utils.implicits

import java.sql.Timestamp
import java.time.Duration

import io.circe.{Encoder, Json}

object Circe {
  implicit val timeStampEncoder: Encoder[java.sql.Timestamp] =
    new Encoder[java.sql.Timestamp] {
      override def apply(t: Timestamp): Json =
        Json.fromString(t.toString.takeWhile(_ != '.'))
    }

  implicit val javaDurationEncoder: Encoder[Duration] =
    new Encoder[Duration] {
      override def apply(duration: Duration): Json = {
        val times = duration.toString.split("-").groupBy(_.last).mapValues(_.mkString)
        val h = times.get('H').map(_.init + "h")
        val m = times.get('M').map(_.init + "m")
        val s = times.get('S').map(s => (s.init.toDouble + 0.5).toInt + "s")
        val string = List(h, m, s).foldLeft("")((str, t) => str + t.fold("")(_ + " "))
        Json.fromString(string)
      }
    }
}
