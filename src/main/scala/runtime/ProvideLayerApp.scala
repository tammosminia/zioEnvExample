package runtime

import zio.{RIO, Unsafe, ZLayer, durationInt}

object ProvideLayerApp extends App {
  Unsafe.unsafe { implicit unsafe =>
    Console.println("Example app start")
    val runtime: zio.Runtime[Any] = zio.Runtime.default
    run()
    run()
    Console.println("Example app end")

    def domainAction: RIO[Database, Unit] = Database.write("new domain object")

    def run(): Unit = runtime.unsafe.run(
      domainAction.provideLayer(AppConfig.layer >>> Database.layer)
    ).getOrThrowFiberFailure()
  }
}
