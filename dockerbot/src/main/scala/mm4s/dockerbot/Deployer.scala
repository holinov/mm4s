package mm4s.dockerbot


import akka.actor.{Status, Actor, ActorLogging, Props}
import com.shekhargulati.reactivex.docker.client.RxDockerClient
import com.shekhargulati.reactivex.docker.client.representations.{DockerContainerRequestBuilder, DockerContainerResponse, HostConfigBuilder, PortBinding}
import com.shekhargulati.reactivex.rxokhttp.HttpStatus
import mm4s.dockerbot.DeploymentModels._
import rx.lang.scala.JavaConversions._
import rx.lang.scala.Observable

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

object Deployer {
  def props() = Props(new Deployer)
}

class Deployer extends Actor with ActorLogging {
  def receive: Receive = {
    case d @ DeployRequest(name, image, ports) =>
      val portmap = ports
                    .map(_.split(":"))
                    .filter(_.length == 2)
                    .map(a => a(0) -> a(1))

      val bindings = portmap
                     .map(p => p._2 -> PortBinding.of("0.0.0.0", p._1))
                     .map(t => t._1 -> Seq(t._2).asJava)
                     .toMap

      val s = sender()
      val client = RxDockerClient.fromDefaultEnv()

      val hostcfg = new HostConfigBuilder().setPortBindings(bindings).createHostConfig()

      val request = new DockerContainerRequestBuilder()
                    .setImage(image)
                    .setHostConfig(hostcfg)
                    .addExposedPort(portmap.map(_._2): _*)
                    .setEnv(List(s"SERVICE_NAME=$name", s"SERVICE_TAGS=dockerbot"))
                    .createDockerContainerRequest()

      val obs: Observable[DockerContainerResponse] = client.createContainerObs(request, name)
      obs.flatMap { r =>
        val o: Observable[HttpStatus] = client.startContainerObs(r.getId)
        o.map(x => DeployResult(r.getId, d))
      }.subscribe(
        r => s ! r,
        e => s ! Status.Failure(e)
      )
  }
}
