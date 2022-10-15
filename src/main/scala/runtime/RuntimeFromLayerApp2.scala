package runtime

import zio.{RIO, Runtime, Unsafe, ZLayer, durationInt}

object RuntimeFromLayerApp2 extends App {
  Unsafe.unsafe { implicit unsafe =>
    Console.println("Example app start")
    val runtime: Runtime.Scoped[AppConfig] = zio.Runtime.unsafe.fromLayer(AppConfig.layer)
    run()
    run()
    runtime.unsafe.shutdown()
    Console.println("Example app end")

    def domainAction: RIO[Database, Unit] = Database.write("new domain object")

    def run(): Unit = runtime.unsafe.run(
      domainAction.provideLayer(Database.layer)
    ).getOrThrowFiberFailure()
  }
}
