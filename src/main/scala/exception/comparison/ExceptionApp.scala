package exception.comparison

import zio.{Console, RIO, Schedule, Task, ZIO, ZIOAppDefault, ZLayer, durationInt}

import java.io.IOException

object ExceptionApp extends App {
  def handleException(t: Throwable): Int = {
    println(t)
    0
  }

  object Ex {
    def f1: Int = throw new RuntimeException("exceptional problems")
    def f2(i: Int): Int = i + 1

    f2(f1)
    def exception: Int = try {
      val a = f1
      f2(a)
    } catch {
      case t: Throwable => handleException(t)
    }
  }

  object Ei {
    def f1: Either[Exception, Int] = Left(new RuntimeException("either problems"))
    def f2(i: Int): Either[Exception, Int] = Right(i + 1)

    def either: Int = (for {
      a <- f1
      r <- f2(a)
    } yield r).fold(handleException, identity)
  }

  object Z {
    def f1: Task[Int] = ZIO.fail(new RuntimeException("zio problems"))
    def f2(i: Int): Task[Int] = ZIO.succeed(i + 1)

    def zio: Task[Int] = (for {
      a <- f1
      r <- f2(a)
    } yield r).catchAll(t => ZIO.attempt(handleException(t)))
  }

}
