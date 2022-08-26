import zio.RIO

object PaxUpdater {
  def run: RIO[AppConfig, Unit] = update.provideLayer(Redis.layer ++ PaxRepository.layer)

  def update: RIO[PaxRepository with Redis, Unit] = for {
    _ <- zio.Console.printLine("PaxUpdater update start")
    pax <- PaxRepository.read
    _ <- Redis.write(pax)
    _ <- zio.Console.printLine("PaxUpdater update end")
  } yield ()
}
