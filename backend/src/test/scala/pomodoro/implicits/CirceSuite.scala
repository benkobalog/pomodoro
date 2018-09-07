package pomodoro.implicits

import org.scalatest._
import java.time.Duration
import pomodoro.implicits.Circe.durationFormatter

class CirceSuite extends FunSuite with Matchers {

  test("Duration Encoder should return nicely formatted strings") {
    durationFormatter(Duration.ofSeconds(3600)) should be("1h")
    durationFormatter(Duration.ofSeconds(1)) should be("1s")
    durationFormatter(Duration.ofSeconds(3655)) should be("1h 55s")
    durationFormatter(Duration.ofSeconds(3800)) should be("1h 3m 20s")
    durationFormatter(Duration.ofMillis(2645)) should be("2s")
    durationFormatter(Duration.ofMillis(2345)) should be("2s")
  }

}
