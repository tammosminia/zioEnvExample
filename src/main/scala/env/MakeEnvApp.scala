package env

import zio.{RIO, Schedule, ZIO, ZIOAppDefault, ZLayer, durationInt}
import RobotDomain._
import java.io.IOException

object MakeEnvApp extends ZIOAppDefault {
  def run = for {
    _ <- zio.Console.printLine("Example app start")
    _ <- useRobot.provideLayer(robotFromScratchLayer)
    _ <- zio.Console.printLine("Example app end")
  } yield ()

  def useRobot: RIO[Robot, Unit] = for {
    _ <- ZIO.serviceWithZIO[Robot](r => zio.Console.printLine(s"robot $r"))
  } yield ()

  val screwLayer: ZLayer[Any, Nothing, Screw] = ZLayer.succeed(Screw())
  val armLayer: ZLayer[Screw, Nothing, Arm] = ZLayer.fromFunction(Arm(_))
  val legLayer: ZLayer[Screw, Nothing, Leg] = ZLayer.fromFunction(Leg(_))
  val robotLayer: ZLayer[Arm with Leg with Screw, Nothing, Robot] = ZLayer.fromFunction(Robot(_, _))

//  val robotFromScratchLayer: ZLayer[Any, Nothing, Robot] = screwLayer >+> (armLayer ++ legLayer) >>> robotLayer
  val robotFromScratchLayer: ZLayer[Any, Nothing, Robot] = ZLayer.make[Robot](screwLayer, armLayer, legLayer, robotLayer)
}
