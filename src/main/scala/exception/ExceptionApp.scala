package exception

import zio.{RIO, Schedule, Task, ZIO, ZIOAppDefault, ZLayer, durationInt, Console}

object ExceptionApp extends ZIOAppDefault {
  def run = for {
    _ <- zio.Console.printLine("Example app start")
    _ <- ZIO.foreach(Range(0, 5))(safe)
    _ <- zio.Console.printLine("Example app end")
  } yield ()

  def safe(i: Int): Task[Unit] =
    danger(i)
      .catchAll(t => Console.printLine(s"caught failure $t"))
      .catchAllDefect(t => Console.printLine(s"caught defect $t"))

  def danger(i: Int): Task[Unit] = i match {
    case 0 => ZIO.unit
    case 1 => ZIO.fail(new RuntimeException("explicit failure"))
    case 2 => ZIO.attempt(throw new RuntimeException("failure from a caught exception"))
    case 3 => ZIO.die(new RuntimeException("explicit defect"))
    case 4 => uncaught
  }

  def uncaught: Task[Unit] = for {
    u <- ZIO.unit
  } yield throw new RuntimeException("uncaught exception")
}
