package env

import zio.{ZIO, RIO, Schedule, ZIOAppDefault, ZLayer, durationInt}

import java.io.IOException

object CombinedEnvApp extends ZIOAppDefault {
  def run = for {
    _ <- zio.Console.printLine("Example app start")
    _ <- runSchedules.provideLayer(bothLayers)
    _ <- zio.Console.printLine("Example app end")
  } yield ()

  def runSchedules: RIO[Int with String, Unit] = for {
    _ <- ZIO.serviceWithZIO[Int](i => zio.Console.printLine(s"integer $i"))
    _ <- ZIO.serviceWithZIO[String](s => zio.Console.printLine(s"string $s"))
  } yield ()

  val intLayer: ZLayer[Any, IOException, Int] = ZLayer.fromZIO(zio.Console.printLine("create Int layer").as(1))
  val stringLayer: ZLayer[Int, IOException, String] = ZLayer.fromZIO(ZIO.serviceWithZIO[Int](i => zio.Console.printLine("create String layer").as(s"String $i")))

  val bothLayers = intLayer ++ (intLayer >>> stringLayer)
}
