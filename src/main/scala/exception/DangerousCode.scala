package exception

import zio.{Console, RIO, Schedule, Task, ZIO, ZIOAppDefault, ZLayer, durationInt}

object DangerousCode {
  def danger(i: Int): Task[Unit] = i match {
    case 0 => ZIO.unit
    case 1 => ZIO.fail(new RuntimeException("explicit failure"))
    case 2 => ZIO.attempt(throw new RuntimeException("failure from a caught exception"))
    case 3 => ZIO.die(new RuntimeException("explicit defect"))
    case 4 => uncaught
    case 5 => ZIO.fail(new RuntimeException("first exception")).ensuring(ZIO.die(new RuntimeException("second exception")))
    case 6 => ZIO.attempt(throw new StackOverflowError("Fatal error that cannot be caught."))
  }

  def uncaught: Task[Unit] = for {
    u <- ZIO.unit
  } yield throw new RuntimeException("uncaught exception")
}
