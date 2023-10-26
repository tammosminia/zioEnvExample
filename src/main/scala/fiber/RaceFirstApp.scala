package fiber

import zio.{RIO, Schedule, ZIO, ZIOAppDefault, ZLayer, durationInt}

import java.io.IOException

object RaceFirstApp extends ZIOAppDefault {
  def run = for {
    _ <- runWithoutfork("aFirst", countDown("aFirst-A", 5), countDown("aFirst-B", 10))
    _ <- runWithoutfork("bFirst", countDown("bFirst-A", 10), countDown("bFirst-B", 5))
    _ <- runWithfork("aFirstFork", countDown("aFirstFork-A", 5), countDown("aFirstFork-B", 10))
    _ <- runWithfork("bFirstFork", countDown("bFirstFork-A", 10), countDown("bFirstFork-B", 5))
    _ <- runWithoutfork("aFirstError", countDownToError("aFirstError-A", 5), countDownToError("aFirstError-B", 10))
    _ <- runWithoutfork("bFirstError", countDownToError("bFirstError-A", 10), countDownToError("bFirstError-B", 5))
    _ <- runWithfork("aFirstForkError", countDownToError("aFirstForkError-A", 5), countDownToError("aFirstForkError-B", 10))
    _ <- runWithfork("bFirstForkError", countDownToError("bFirstForkError-A", 10), countDownToError("bFirstForkError-B", 5))
  } yield ()

  def runWithoutfork[A](name: String, a: ZIO[Any, Any, A], b: ZIO[Any, Any, A]) = for {
    _ <- zio.Console.printLine(s"$name start")
    r <- ZIO.raceFirst(a, List(b)).catchAll(e => zio.Console.printLine(e.toString))
    _ <- zio.Console.printLine(s"$name end")
  } yield ()

  def runWithfork[A](name: String, a: ZIO[Any, Any, A], b: ZIO[Any, Any, A]) = for {
    _ <- zio.Console.printLine(s"$name start")
    fa <- a.fork
    fb <- b.fork
    r <- ZIO.raceFirst(fa.await, List(fb.await))
    _ <- zio.Console.printLine(r.toString)
    _ <- zio.Console.printLine(s"$name end")
  } yield ()

  def countDown(name: String, i: Int): ZIO[Any, Any, String] =
    if (i == 0) ZIO.succeed(s"$name is 0")
    else zio.Console.printLine(s"counting $name $i") *> ZIO.sleep(100.millis) *> countDown(name, i - 1)

  def countDownToError(name: String, i: Int): ZIO[Any, Any, Nothing] =
    if (i == 0) ZIO.fail(s"$name is 0")
    else zio.Console.printLine(s"counting $name $i") *> ZIO.sleep(100.millis) *> countDownToError(name, i - 1)
}
