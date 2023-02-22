val scala3Version = "3.2.2"

lazy val root = project
  .in(file("."))
  .settings(
    name := "easy-chat-scala",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test,
    libraryDependencies += "io.netty" % "netty-all" % "4.1.89.Final",
    libraryDependencies += "com.alibaba.fastjson2" % "fastjson2" % "2.0.24",
    libraryDependencies += "com.caucho" % "hessian" % "4.0.66"
  )
