lazy val commonSettings = Seq(
  organization := "com.github.jw3",
  description := "Mattermost for Scala",
  version := "0.1-SNAPSHOT",
  licenses +=("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0")),


  scalaVersion := "2.11.7",
  scalacOptions += "-target:jvm-1.8",

  resolvers += "jw3 at bintray" at "https://dl.bintray.com/jw3/maven",
  com.updateimpact.Plugin.apiKey in ThisBuild := sys.env.getOrElse("UPDATEIMPACT_API_KEY", (com.updateimpact.Plugin.apiKey in ThisBuild).value),

  libraryDependencies ++= {
    val akkaVersion = "2.4.2"
    val scalaTest = "3.0.0-M15"

    Seq(
      "com.rxthings" %% "akka-injects" % "0.4",

      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-http-core" % akkaVersion,

      "com.typesafe.akka" %% "akka-http-experimental" % akkaVersion,
      "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaVersion,

      "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion % Runtime,

      "org.scalactic" %% "scalactic" % scalaTest % Test,
      "org.scalatest" %% "scalatest" % scalaTest % Test,
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
    )
  }
)

lazy val root = project.in(file(".")).aggregate(api, bots, examples, dockerbot)

lazy val api = project.in(file("api"))
               .settings(commonSettings: _*)
               .settings(name := "mm4s-api")

lazy val bots = project.in(file("bots"))
                .dependsOn(api)
                .settings(commonSettings: _*)
                .settings(name := "mm4s-bots")

lazy val examples = project.in(file("examples"))
                    .dependsOn(api, bots, dockerbot)
                    .settings(commonSettings: _*)
                    .settings(name := "mm4s-examples")
                    .settings(
                      mainClass in assembly := Some("mm4s.dockerbot.Boot"),
                      dockerRepository := Some("jwiii"),
                      dockerBaseImage := "anapsix/alpine-java:jre8",
                      dockerEntrypoint := Seq("bin/mm4s-dockerbot")
                    )
                    .enablePlugins(JavaAppPackaging)

lazy val dockerbot = project.in(file("dockerbot"))
                     .dependsOn(api, bots)
                     .settings(commonSettings: _*)
                     .settings(name := "mm4s-dockerbot")
                     .settings(
                       mainClass in assembly := Some("mm4s.dockerbot.Boot"),
                       dockerRepository := Some("jwiii"),
                       dockerBaseImage := "anapsix/alpine-java:jre8"
                     )
                     .enablePlugins(JavaAppPackaging)
