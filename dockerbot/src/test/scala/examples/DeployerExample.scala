package examples

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import mm4s.dockerbot.{Deploy, DockerDeployer}

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, DurationInt}

object DeployerExample extends App {
  val system = ActorSystem()
  val deployer = system.actorOf(DockerDeployer.props())
  implicit val timeout = Timeout(10.seconds)

  val r = deployer ? Deploy("testing", "mattermost/platform:2.0", Seq("0:80"))
  val id = Await.result(r, timeout.duration)

  println(id)

  system.terminate()
  Await.ready(system.whenTerminated, Duration.Inf)
}
