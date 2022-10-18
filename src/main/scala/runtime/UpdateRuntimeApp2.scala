package runtime

import zio.{RIO, Runtime, Scope, Unsafe, ZEnvironment, ZIO, ZLayer, durationInt}

object UpdateRuntimeApp2 extends App {
  Unsafe.unsafe { implicit unsafe =>
    Console.println("Example app start")
    val runtime: Runtime.Scoped[AppConfig] = zio.Runtime.unsafe.fromLayer(AppConfig.layer)
    val updatedRuntime = runtime.unsafe.run {
      val z1: ZIO[AppConfig with Scope, Throwable, ZEnvironment[Database]] = Database.layer.build
        val z: ZIO[AppConfig with Scope, Throwable, Runtime.Scoped[AppConfig with Database]] = z1.map(dbe => runtime.mapEnvironment(_ ++ dbe))
      z
    }.getOrThrowFiberFailure()
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
