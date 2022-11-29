package dsenv

import zio.{RIO, Scope, ULayer, ZIO, ZLayer}

class PaxRepository(val ds: Datasource) {
  def write(domain: String): RIO[Datasource, Unit] = Datasource.write(domain)
}

object PaxRepository {
  def acquire: RIO[Datasource, PaxRepository] = ZIO.serviceWith[Datasource](ds => new PaxRepository(ds))
  def release(fr: => PaxRepository): ZIO[Any, Nothing, Unit] = ZIO.unit
  def scope: RIO[Scope with Datasource, PaxRepository] = ZIO.acquireRelease(acquire)(release(_))
  val layer: ZLayer[Datasource, Throwable, PaxRepository] = ZLayer.scoped(scope)

  implicit def prToDs[A](dsz: RIO[Datasource, A]): RIO[PaxRepository, A] = ZIO.serviceWithZIO[PaxRepository](pr => dsz.provide(ZLayer.succeed(pr.ds)))

  def write(domain: String): RIO[PaxRepository, Unit] = ZIO.serviceWithZIO[PaxRepository](pr => prToDs(pr.write(domain)))
}