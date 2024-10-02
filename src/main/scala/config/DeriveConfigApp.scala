package config

import zio.config._
import zio.config.magnolia._
import zio.config.typesafe.TypesafeConfigProvider
import zio.{Config, ZIO, ZIOAppDefault}

import java.nio.file.Paths

object DeriveConfigApp extends ZIOAppDefault {
  case class CoffeeConfig(ingredients: List[String], brewSeconds: Int)
  case class AppConfig(coffee: CoffeeConfig)

//  This is not used in deriveConfig[AppConfig]
//  implicit val stringListDescriptor: Config[List[String]] = deriveConfig[String].map(_.split(",").toList)
  implicit val stringListDescriptor: DeriveConfig[List[String]] = DeriveConfig[String].map(_.split(",").toList)
  implicit val appConfigDescriptor: Config[AppConfig] = deriveConfig[AppConfig]

  def readResource[A](filePath: String)(implicit config: Config[A]): ZIO[Any, Config.Error, A] = {
    val file = Paths.get(getClass.getClassLoader.getResource(filePath).toURI).toFile
    val c = config.from(TypesafeConfigProvider.fromHoconFile(file))
    read(c)
  }

  def run = for {
    appConfig <- readResource[AppConfig]("app.conf")
    coffee = appConfig.coffee
    console <- ZIO.console
    _ <- console.printLine("start brewing coffee")
    _ <- ZIO.foreach(coffee.ingredients)(i => console.printLine(s"adding ingredient: $i"))
    _ <- console.printLine(s"waiting for ${coffee.brewSeconds} seconds")
    _ <- console.printLine(s"done. enjoy!")
  } yield ()
}
