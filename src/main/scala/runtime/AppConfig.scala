package runtime

import zio.{Task, ZLayer, ZIO}

/** Contains all connection configuration for the app */
case class AppConfig(database: String)

object AppConfig {
  def read: AppConfig = {
    println("reading AppConfig")
    AppConfig("databaseHost")
  }
  def make: Task[AppConfig] = ZIO.attempt(read)
  def layer: ZLayer[Any, Throwable, AppConfig] = ZLayer.apply(make)
}

