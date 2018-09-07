package object pomodoro {

  implicit class PipeOps[A](a: A) {
    def |>[B](f: A => B): B = f(a)
  }
}
