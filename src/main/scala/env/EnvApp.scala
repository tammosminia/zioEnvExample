package env

import zio.{RIO, Schedule, ZIOAppDefault, ZLayer, durationInt}

object EnvApp extends ZIOAppDefault {
  def run = for {
    _ <- zio.Console.printLine("Example app start")
    appConfig = AppConfig("flightHost", "paxHost", "redisHost")
    acLayer = ZLayer.succeed(appConfig)
    _ <- runSchedules.provideLayer(acLayer)
    _ <- zio.Console.printLine("Example app end")
  } yield ()

  def runSchedules: RIO[AppConfig, Unit] = for {
    _ <- FlightUpdater.run.schedule(Schedule.fixed(2.seconds).forever).fork
    _ <- PaxUpdater.run.schedule(Schedule.fixed(5.seconds).forever)
  } yield ()
}
