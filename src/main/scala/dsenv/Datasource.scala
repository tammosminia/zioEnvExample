package dsenv

import zio.{RIO, Scope, ZIO, ZLayer}

class Datasource(config: String) {
  def write(domain: String): RIO[Any, Unit] = ZIO.succeed(())
}

object Datasource {
  val layer = ZLayer.succeed(new Datasource("dsConf"))

  def write(domain: String): RIO[Datasource, Unit] = ZIO.serviceWithZIO[Datasource](_.write(domain))
}