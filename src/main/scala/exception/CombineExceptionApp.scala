package exception

import zio.{Console, RIO, Schedule, Task, ZIO, ZIOAppDefault, ZLayer, durationInt}

object CombineExceptionApp extends ZIOAppDefault {
  def run = (for {
        i <- getConfig
        _ <- zio.Console.printLine(s"parsed: $i")
      } yield ()
    ).catchAllCause(c => Console.printLine(s"caught cause $c"))

  class ReadFileException(message: String) extends Exception(message)
  class ParseException(cause: Throwable) extends Exception(cause)

  def readFile(filename: String): ZIO[Any, ReadFileException, String] = ZIO.succeed("file content")

  def parse(s: String): ZIO[Any, ParseException, Int] = ZIO.attempt(s.toInt).mapError(t => ParseException(t))

  def readAndParse(filename: String): ZIO[Any, ReadFileException | ParseException, Int] = for {
    content <- readFile(filename)
    parsed <- parse(content)
  } yield parsed

  def getConfig: ZIO[Any, Nothing, Int] = readAndParse("config").orDie
}
