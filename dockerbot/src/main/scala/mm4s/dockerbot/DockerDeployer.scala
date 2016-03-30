package mm4s.dockerbot


import akka.actor.{Actor, ActorLogging, Props}
import com.shekhargulati.reactivex.docker.client.RxDockerClient
import com.shekhargulati.reactivex.docker.client.representations.{DockerContainerRequestBuilder, DockerContainerResponse}
import com.shekhargulati.reactivex.rxokhttp.HttpStatus
import mm4s.dockerbot.DeployModels._
import rx.lang.scala.JavaConversions._
import rx.lang.scala.Observable

import scala.collection.JavaConversions._

/**
 * Provide actor based interface to deploy
 */
object DockerDeployer {
  def props() = Props(new DockerDeployer)
}

class DockerDeployer extends Actor with ActorLogging {
  def receive: Receive = {
    case d @ Deploy(name, image, ports) =>
      val s = sender()
      val client = RxDockerClient.fromDefaultEnv()
      val request = new DockerContainerRequestBuilder()
                    .setImage(image)
                    .addExposedPort(ports: _*)
                    .setEnv(List(s"SERVICE_NAME=$name", s"SERVICE_TAGS=dockerbot"))
                    .createDockerContainerRequest()

      val obs: Observable[DockerContainerResponse] = client.createContainerObs(request, name)
      obs.flatMap { r =>
        val o: Observable[HttpStatus] = client.startContainerObs(r.getId)
        o.map(x => DeployResult(r.getId, d))
      }.subscribe(r => s ! r)
  }
}

