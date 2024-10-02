package config

import zio.config._
import zio.config.magnolia._
import zio.config.typesafe.TypesafeConfigProvider
import zio.{Config, ZIO, ZIOAppDefault}

import java.nio.file.Paths



object DeriveConfigApp extends ZIOAppDefault {
  case class CoffeeConfig(ingredients: List[String], brewSeconds: Int)
  case class AppConfig(coffee: CoffeeConfig)

  implicit val coffeeConfigDescriptor: Config[CoffeeConfig] = deriveConfig[CoffeeConfig]
  implicit val appConfigDescriptor: Config[AppConfig] = deriveConfig[AppConfig]

  def readResource[A](filePath: String)(implicit config: Config[A]): ZIO[Any, Config.Error, A] = {
    val file = Paths.get(getClass.getClassLoader.getResource(filePath).toURI).toFile
    val c = config.from(TypesafeConfigProvider.fromHoconFile(file))
    read(c)
  }

  def run = for {
    appConfig <- readResource[AppConfig]("app.conf")
    coffee = appConfig.coffee
    _ <- ZIO.logInfo("start brewing coffee")
    _ <- ZIO.foreach(coffee.ingredients)(i => ZIO.logInfo(s"adding ingredient: $i"))
    _ <- ZIO.logInfo(s"waiting for ${coffee.brewSeconds} seconds")
    _ <- ZIO.logInfo(s"done. enjoy!")
  } yield ()
}
