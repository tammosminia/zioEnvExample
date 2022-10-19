package runtime

import zio.{RIO, Runtime, Scope, Unsafe, ZEnvironment, ZIO, ZLayer, durationInt}

object UpdateRuntimeAppWithUserId extends App {
  Unsafe.unsafe { implicit unsafe =>
    Console.println("Example app start")
    val runtime: Runtime.Scoped[Database] = zio.Runtime.unsafe.fromLayer(AppConfig.layer >>> Database.layer)
    case class UserId(id: String)
    case class TrackingId(id: Int)
    val userId = UserId("123")
    val updatedRuntime: Runtime.Scoped[Database with UserId] = runtime.mapEnvironment(_.add(userId))
    run(TrackingId(1))
    run(TrackingId(2))
    updatedRuntime.unsafe.shutdown()
    Console.println("Example app end")

    def domainAction: RIO[Database with UserId with TrackingId, Unit] = for {
      u <- ZIO.service[UserId]
      t <- ZIO.service[TrackingId]
      r <- Database.write(s"new domain object for user ${u.id}, trackingId: ${t.id}")
    } yield r

    def run(t: TrackingId): Unit = {
      updatedRuntime.unsafe.run(domainAction.provideSome[Database with UserId](ZLayer.succeed(t))).getOrThrowFiberFailure()
    }
  }
}
