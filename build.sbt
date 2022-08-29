resolvers += Resolver.sonatypeRepo("public")

lazy val zioVersion = "2.0.1"

lazy val root = (project in file("."))
  .settings(
    inThisBuild(
      List(
        scalaVersion := "3.1.3"
      )
    ),
    name := "zio Env example",
    libraryDependencies ++= Seq(
  	"dev.zio" %% "zio" % zioVersion
    ),
    scalacOptions ++= Seq(
      "-language:postfixOps",
      "-language:implicitConversions",
      "-deprecation"
    )
  )
