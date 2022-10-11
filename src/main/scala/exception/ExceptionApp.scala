package exception

import zio.{RIO, Schedule, Task, ZIO, ZIOAppDefault, ZLayer, durationInt, Console}

object ExceptionApp extends ZIOAppDefault {
  def run = for {
    _ <- zio.Console.printLine("Example app start")
    _ <- ZIO.foreach(Range(0, Int.MaxValue))(safe)
    _ <- zio.Console.printLine("Example app end")
  } yield ()

  def safe(i: Int): Task[Unit] =
    (DangerousCode.danger(i) *> Console.printLine("ok"))
      .catchAll(t => Console.printLine(s"caught failure $t"))
      .catchAllDefect(t => Console.printLine(s"caught defect $t"))
}
