package env

import zio.{RIO, Scope, ZIO, ZLayer}

class PaxRepository(config: String) {
  println(s"opening PaxRepository $config")
  def read: RIO[Any, String] = ZIO.succeed("Pax")
  def close(): Unit = println("closing PaxRepository")
}

object PaxRepository {
  def acquire: RIO[AppConfig, PaxRepository] = ZIO.serviceWith[AppConfig](ac => new PaxRepository(ac.pax))
  def release(fr: => PaxRepository): ZIO[Any, Nothing, Unit] = ZIO.succeedBlocking(fr.close())
  def scope: RIO[Scope with AppConfig, PaxRepository] = ZIO.acquireRelease(acquire)(release(_))
  val layer: ZLayer[AppConfig, Throwable, PaxRepository] = ZLayer.scoped(scope)
  def read: RIO[PaxRepository, String] = ZIO.serviceWithZIO[PaxRepository](_.read)
}