import zio.{RIO, Scope, ZIO, ZLayer}

class Redis(config: String) {
  println(s"opening Redis $config")
  def write(domain: String): RIO[Any, Unit] = ZIO.succeed(())
  def close(): Unit = println("closing Redis")
}

object Redis {
  def acquire: RIO[AppConfig, Redis] = ZIO.serviceWith[AppConfig](ac => new Redis(ac.redis))
  def release(fr: => Redis): ZIO[Any, Nothing, Unit] = ZIO.succeedBlocking(fr.close())
  def scope: RIO[Scope with AppConfig, Redis] = ZIO.acquireRelease(acquire)(release(_))
  val layer: ZLayer[AppConfig, Throwable, Redis] = ZLayer.scoped(scope)

  def write(domain: String): RIO[Redis, Unit] = ZIO.serviceWithZIO[Redis](_.write(domain))
}