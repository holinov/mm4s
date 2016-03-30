package mm4s.dockerbot

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 * A Dockerbot is a Docker API Bot that provides
 * Docker container deployment as a Microservice
 */
object Boot extends App with LazyLogging {
  implicit val system = ActorSystem("dockerbot-deployer")
  implicit val materialize = ActorMaterializer()

  val deployer = system.actorOf(Deployer.props())
  val http = HttpInterface(deployer)

  Await.ready(system.whenTerminated, Duration.Inf)
}
