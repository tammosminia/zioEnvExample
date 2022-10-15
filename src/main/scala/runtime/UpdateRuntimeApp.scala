package runtime

import zio.{RIO, Runtime, Scope, Unsafe, ZIO, ZLayer, durationInt}

object UpdateRuntimeApp extends App {
  Unsafe.unsafe { implicit unsafe =>
    Console.println("Example app start")
    val runtime: Runtime.Scoped[AppConfig] = zio.Runtime.unsafe.fromLayer(AppConfig.layer)
    val updatedRuntime: Runtime.Scoped[AppConfig with Database] = runtime.unsafe.run(
      Database.acquire.map(db => runtime.mapEnvironment(_.add(db)))
    ).getOrThrowFiberFailure()
    run()
    run()
    updatedRuntime.unsafe.shutdown()

    Console.println("Example app end")

    def domainAction: RIO[Database, Unit] = Database.write("new domain object")

    def run(): Unit = {
      updatedRuntime.unsafe.run(domainAction).getOrThrowFiberFailure()
    }
  }
}
