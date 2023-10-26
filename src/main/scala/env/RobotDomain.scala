package env

object RobotDomain {
  case class Screw()
  case class Arm(screw: Screw)
  case class Leg(screw: Screw)
  case class Robot(arm: Arm, leg: Leg)
}
