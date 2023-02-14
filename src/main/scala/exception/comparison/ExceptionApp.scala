package exception.comparison

import zio.{Console, RIO, Schedule, Task, ZIO, ZIOAppDefault, ZLayer, durationInt}

import java.io.IOException

object ExceptionApp {
  object UseException {
    def f1: Int = throw new RuntimeException("exceptional problems")
    def f2(i: Int): Int = i + 1

    def combined: Int = {
      val a = f1
      f2(a)
    }

    def handled: Int = try {
      combined
    } catch {
      case t: Throwable => 0
    }
  }

  object UseEither {
    def f1: Either[Exception, Int] = Left(new RuntimeException("either problems"))
    def f2(i: Int): Either[Exception, Int] = Right(i + 1)

    def combined: Either[Exception, Int] = for {
      a <- f1
      r <- f2(a)
    } yield r

    def handled: Int = combined.fold(t => 0, identity)
  }

  object UseZIO {
    def f1: Task[Int] = ZIO.fail(new RuntimeException("zio problems"))
    def f2(i: Int): Task[Int] = ZIO.succeed(i + 1)

    def combined: Task[Int] = for {
      a <- f1
      r <- f2(a)
    } yield r

    def handled: Task[Int] = combined.catchAll(t => ZIO.succeed(0))
  }

  object EitherWithSpecificExceptions {
    class GenerateError
    class AddError

    def f1: Either[GenerateError, Int] = Left(new GenerateError)
    def f2(i: Int): Either[AddError, Int] = Right(i + 1)

    def combined: Either[GenerateError | AddError, Int] = for {
      a <- f1
      r <- f2(a)
    } yield r

    def handled: Int = combined.fold(t => 0, identity)
  }

}
