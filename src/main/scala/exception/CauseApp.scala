package exception

import zio.{Console, RIO, Schedule, Task, ZIO, ZIOAppDefault, ZLayer, durationInt}

object CauseApp extends ZIOAppDefault {
  def run = for {
    _ <- zio.Console.printLine("Example app start")
    _ <- ZIO.foreach(Range(0, Int.MaxValue))(safe)
    _ <- zio.Console.printLine("Example app end")
  } yield ()

  def safe(i: Int): Task[Unit] =
    DangerousCode.danger(i)
      .catchAllCause(c => Console.printLine(s"caught cause $c"))
}
