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
import mm4s.dockerbot.DeployModels._
import mm4s.dockerbot.DeployProtocols._

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
        entity(as[Deploy]) { d =>
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

  Http().bindAndHandle(routes, "localhost", 9999)
}
