import akka.actor.ActorSystem

import scala.concurrent.ExecutionContext

package object mm4s {
  val mmapi: String = "/api/v1"


  //\\ implicits //\\

  // use inscope ActorSystem dispatcher
  implicit def sys2ec(implicit system: ActorSystem): ExecutionContext = system.dispatcher
}
