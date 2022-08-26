import zio.RIO

object FlightUpdater {
  def run: RIO[AppConfig, Unit] = update.provideLayer(Redis.layer ++ FlightRepository.layer)

  def update: RIO[FlightRepository with Redis, Unit] = for {
    _ <- zio.Console.printLine("FlightUpdater update start")
    flight <- FlightRepository.read
    _ <- Redis.write(flight)
    _ <- zio.Console.printLine("FlightUpdater update end")
  } yield ()
}
