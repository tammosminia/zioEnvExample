package runtime

import zio.{RIO, Runtime, Scope, Unsafe, ZEnvironment, ZIO, ZLayer, durationInt}

object UpdateRuntimeAppWithUserId extends App {
  Unsafe.unsafe { implicit unsafe =>
    Console.println("Example app start")
    val runtime: Runtime.Scoped[AppConfig with Database] = zio.Runtime.unsafe.fromLayer(AppConfig.layer ++ (AppConfig.layer >>> Database.layer))
    case class UserId(id: String)
    val userId = UserId("123")
    val updatedRuntime = runtime.mapEnvironment(_.add(userId))
    run()
    run()
    updatedRuntime.unsafe.shutdown()
    Console.println("Example app end")

    def domainAction: RIO[Database with UserId, Unit] = ZIO.serviceWithZIO[UserId] { u =>
      Database.write(s"new domain object for user ${u.id}")
    }

    def run(): Unit = {
      updatedRuntime.unsafe.run(domainAction).getOrThrowFiberFailure()
    }
  }
}
