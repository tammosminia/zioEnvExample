resolvers += Resolver.sonatypeRepo("public")

lazy val zioVersion = "2.1.6"
lazy val zioConfigVersion = "4.0.2"

lazy val root = (project in file("."))
  .settings(
    inThisBuild(
      List(
        scalaVersion := "2.13.14"
      )
    ),
    name := "zio Env example",
    libraryDependencies ++= Seq(
  	  "dev.zio" %% "zio" % zioVersion,
      "dev.zio" %% "zio-config"          % zioConfigVersion,
      "dev.zio" %% "zio-config-magnolia" % zioConfigVersion,
      "dev.zio" %% "zio-config-typesafe" % zioConfigVersion,
    ),
    scalacOptions ++= Seq(
      "-language:postfixOps",
      "-language:implicitConversions",
      "-deprecation"
    )
  )
