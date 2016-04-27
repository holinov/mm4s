package mm4s.dockerbot

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import mm4s.dockerbot.DeploymentModels._
import mm4s.dockerbot.DeploymentProtocols._
import net.ceedubs.ficus.Ficus._

import scala.concurrent.duration.DurationInt


object HttpInterface {
  def apply(deployer: ActorRef)(implicit system: ActorSystem, mat: ActorMaterializer) = {
    new HttpInterface(deployer)
  }
}

class HttpInterface(deployer: ActorRef)(implicit val actorSystem: ActorSystem, val materializer: ActorMaterializer) extends LazyLogging {
  implicit val timeout = Timeout(10.seconds)

  val routes =
    post {
      path("deploy") {
        entity(as[DeployRequest]) { d =>
          complete {
            (deployer ? d).mapTo[DeployResult]
          }
        }
      }
    } ~
      get {
        path("ping") {
          complete(StatusCodes.OK)
        }
      }

  val cfg = Configuration.build()
  val host = cfg.as[String](Configuration.key.host)
  val port = cfg.as[Int](Configuration.key.port)
  logger.debug("binding to [{}:{}]", host, port.toString)
  Http().bindAndHandle(routes, host, port)
}
