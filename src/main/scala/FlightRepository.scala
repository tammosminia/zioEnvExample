import zio.{RIO, Scope, ZIO, ZLayer}

class FlightRepository(config: String) {
  println(s"opening FlightRepository $config")
  def read: RIO[Any, String] = ZIO.succeed("Flight")
  def close(): Unit = println("closing FlightRepository")
}

object FlightRepository {
  def acquire: RIO[AppConfig, FlightRepository] = ZIO.serviceWith[AppConfig](ac => new FlightRepository(ac.flight))
  def release(fr: => FlightRepository): ZIO[Any, Nothing, Unit] = ZIO.succeedBlocking(fr.close())
  def scope: RIO[Scope with AppConfig, FlightRepository] = ZIO.acquireRelease(acquire)(release(_))
  val layer: ZLayer[AppConfig, Throwable, FlightRepository] = ZLayer.scoped(scope)
  def read: RIO[FlightRepository, String] = ZIO.serviceWithZIO[FlightRepository](_.read)
}