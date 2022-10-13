package runtime

import zio.{RIO, Unsafe, ZLayer, durationInt}

object RuntimeFromLayerApp extends App {
  Unsafe.unsafe { implicit unsafe =>
    Console.println("Example app start")
    val runtime = zio.Runtime.unsafe.fromLayer(AppConfig.layer >>> Database.layer)
    run()
    run()
    runtime.unsafe.shutdown()
    Console.println("Example app end")

    def domainAction: RIO[Database, Unit] = Database.write("new domain object")

    def run(): Unit = runtime.unsafe.run(domainAction).getOrThrowFiberFailure()
  }
}
