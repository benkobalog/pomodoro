package utils.implicits

import java.sql.Timestamp
import java.time.Duration

import io.circe.{Encoder, Json}

object Circe {

  def createEncoder[A](fn: A => Json): Encoder[A] = new Encoder[A] {
    override def apply(a: A): Json = fn(a)
  }

  implicit val timeStampEncoder: Encoder[java.sql.Timestamp] =
    createEncoder(
      timestamp => Json.fromString(timestamp.toString.takeWhile(_ != '.')))

  implicit val javaDurationEncoder: Encoder[Duration] =
    createEncoder { duration =>
      val times =
        duration.toString.split("-").groupBy(_.last).mapValues(_.mkString)
      val h = times.get('H').map(_.init + "h")
      val m = times.get('M').map(_.init + "m")
      val s = times.get('S').map(s => (s.init.toDouble + 0.5).toInt + "s")
      val string =
        List(h, m, s).foldLeft("")((str, t) => str + t.fold("")(_ + " "))
      Json.fromString(string)
    }
}
