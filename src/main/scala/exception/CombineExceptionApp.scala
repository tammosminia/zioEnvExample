package exception

import zio.{Console, RIO, Schedule, Task, ZIO, ZIOAppDefault, ZLayer, durationInt}

object CombineExceptionApp extends ZIOAppDefault {
  def run = for {
    _ <- zio.Console.printLine("Example app start")
    i <- readAndParse
    _ <- zio.Console.printLine(s"parsed: $i")
    _ <- zio.Console.printLine("Example app end")
  } yield ()

  case class ReadFileException(message: String)
  case class ParseException(message: String)

  def readFile: ZIO[Any, ReadFileException, String] = ZIO.succeed("file content")

  def parse(s: String): ZIO[Any, ParseException, Int] = ZIO.attempt(s.toInt).mapError(t => ParseException(t.getMessage))

  def readAndParse: ZIO[Any, Product, Int] = for {
    content <- readFile
    parsed <- parse(content)
  } yield parsed
}
