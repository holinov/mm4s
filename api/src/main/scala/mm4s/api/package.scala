package mm4s

import akka.actor.ActorSystem

import scala.concurrent.ExecutionContext

package object api {
  val mmapi: String = "/api/v1"

  def uripath(suffix: String) = s"$mmapi$suffix"


  //\\ implicits //\\

  // use inscope ActorSystem dispatcher
  implicit def sys2ec(implicit system: ActorSystem): ExecutionContext = system.dispatcher
}
