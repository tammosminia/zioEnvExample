package runtime

import zio.{Task, ZLayer, ZIO}

/** Contains all connection configuration for the app */
case class AppConfig(database: String)

object AppConfig {
  def read: Task[AppConfig] = ZIO.attempt(AppConfig("databaseHost"))
  def layer: ZLayer[Any, Throwable, AppConfig] = ZLayer.apply(read)
}

