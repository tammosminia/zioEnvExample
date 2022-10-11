package exception

import zio._

object DangerousCode {
  def danger(i: Int): ZIO[Any, Any, Unit] = i match {
    case 0 => ZIO.unit
    case 1 => ZIO.fail("explicit failure")
    case 2 => ZIO.attempt(throw new RuntimeException("failure from a caught exception"))
    case 3 => ZIO.die(new RuntimeException("explicit defect"))
    case 4 => ZIO.unit.map(_ => throw new RuntimeException("uncaught exception"))
    case 5 => ZIO.fail(new RuntimeException("first exception")).ensuring(ZIO.die(new RuntimeException("second exception")))
    case 6 => ZIO.attempt(throw new StackOverflowError("Fatal error that cannot be caught."))
  }
}
