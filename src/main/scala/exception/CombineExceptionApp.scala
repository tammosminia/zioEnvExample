package exception

import zio.{Console, IO, RIO, Schedule, Task, URIO, ZIO, ZIOAppDefault, ZLayer, durationInt}

import java.io.IOException

object CombineExceptionApp extends ZIOAppDefault {
  def run = (for {
        i <- getConfig
        _ <- writeOutput(i)
      } yield ()
    ).catchAllCause(c => Console.printLine(s"caught cause $c"))

  class ParseException(cause: Throwable) extends Exception(cause)

  def readFile(filename: String): ZIO[Any, IOException, String] = ZIO.succeed("file content")

  def writeFile(filename: String, content: String): ZIO[Any, IOException, Nothing] = ZIO.fail(IOException("cannot write"))

  //Here we wrap the NumberFormatException from toInt
  def parse(s: String): ZIO[Any, ParseException, Int] = ZIO.attempt(s.toInt).mapError(t => ParseException(t))

  //Here we combine 2 exception types to send them up the stack
  def readAndParse(filename: String): ZIO[Any, IOException | ParseException, Int] = for {
    content <- readFile(filename)
    parsed <- parse(content)
  } yield parsed

  //Here we silently ignore the exception.
  //In a real application, you probably want to log an exception before ignoring it.
  //And consider killing/restarting the application if logging doesn't work.
  def log(t: Throwable): ZIO[Any, Nothing, Unit] = Console.printLine(t.getMessage).ignore

  //Here we log the Exception, then fall back to a default result
  def getConfig: ZIO[Any, Nothing, Int] = readAndParse("config").tapError(log).orElseSucceed(0)

  //Here we try writing once more, then after that fails, change the failure into a defect.
  def writeOutput(i: Int): ZIO[Any, Nothing, Nothing] = writeFile("output", s"parsed: $i").tapError(log).retryN(1).orDie
}
