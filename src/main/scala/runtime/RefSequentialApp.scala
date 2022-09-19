package runtime

import zio.{Console, RIO, Ref, Runtime, Schedule, Task, Unsafe, ZIO, ZIOAppDefault, ZLayer, durationInt}

import java.io.IOException

object RefSequentialApp extends App {
  val countLayer: ZLayer[Any, Nothing, Ref[Int]] = ZLayer(Ref.make(0))
  def increase: ZIO[Ref[Int], Nothing, Any] = ZIO.serviceWithZIO[Ref[Int]](r => r.update(c => c + 1))
  def get: ZIO[Ref[Int], Nothing, Int] = ZIO.serviceWithZIO[Ref[Int]](r => r.get)

  Unsafe.unsafe { implicit u =>
    val runtime: Runtime[Ref[Int]] = zio.Runtime.unsafe.fromLayer(countLayer)
    val z: ZIO[Ref[Int], IOException, Int] = for {
      _ <- increase
      c <- get
      _ <- Console.printLine(c)
    } yield c
    runtime.unsafe.run(z)
    runtime.unsafe.run(z)
  }
}
