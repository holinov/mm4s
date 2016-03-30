package mm4s.dockerbot


import akka.actor.{Actor, ActorLogging, Props}
import com.shekhargulati.reactivex.docker.client.RxDockerClient
import com.shekhargulati.reactivex.docker.client.representations.{DockerContainerRequestBuilder, DockerContainerResponse}
import com.shekhargulati.reactivex.rxokhttp.HttpStatus
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
    case d: Deploy =>
      val s = sender()
      val client = RxDockerClient.fromDefaultEnv()
      val request = new DockerContainerRequestBuilder()
                    .setImage(d.image)
                    .addExposedPort(d.ports: _*)
                    .setEnv(List(s"SERVICE_NAME=${d.name}", s"SERVICE_TAGS=dockerbot"))
                    .createDockerContainerRequest()

      val obs: Observable[DockerContainerResponse] = client.createContainerObs(request, d.name)
      obs.flatMap { r =>
        val o: Observable[HttpStatus] = client.startContainerObs(r.getId)
        o.map(x => r.getId)
      }.subscribe(r => s ! r)
  }
}

