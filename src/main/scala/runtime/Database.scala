package runtime

import zio.{RIO, Scope, ZIO, ZLayer, Console}

class Database(config: String) {
  println(s"opening Database connection")
  def write(domain: String): RIO[Any, Unit] = Console.printLine(s"writing $domain")
  def close(): Unit = println("closing Database connection")
}

object Database {
  def acquire: RIO[AppConfig, Database] = ZIO.serviceWith[AppConfig](ac => new Database(ac.database))
  def release(fr: => Database): ZIO[Any, Nothing, Unit] = ZIO.succeedBlocking(fr.close())
  def scope: RIO[Scope with AppConfig, Database] = ZIO.acquireRelease(acquire)(release(_))
  val layer: ZLayer[AppConfig, Throwable, Database] = ZLayer.scoped(scope)

  def write(domain: String): RIO[Database, Unit] = ZIO.serviceWithZIO[Database](_.write(domain))
}