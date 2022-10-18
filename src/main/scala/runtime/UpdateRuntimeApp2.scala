package runtime

import zio.{RIO, Runtime, Scope, Unsafe, ZEnvironment, ZIO, ZLayer, durationInt}

object UpdateRuntimeApp2 extends App {
  Unsafe.unsafe { implicit unsafe =>
    Console.println("Example app start")
    val runtime: Runtime.Scoped[AppConfig] = zio.Runtime.unsafe.fromLayer(AppConfig.layer)
    val dbEnv: ZEnvironment[Database] = runtime.unsafe.run {
      Database.layer.build(Scope.global)
    }.getOrThrowFiberFailure()
    val updatedRuntime: Runtime.Scoped[AppConfig with Database] = runtime.mapEnvironment(_ ++ dbEnv)
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
