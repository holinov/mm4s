package mm4s

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.scaladsl.Flow

import scala.concurrent.ExecutionContext

package object api {
  type Connection = Flow[HttpRequest, HttpResponse, _]
  val mmapi: String = "/api/v1"

  def uripath(suffix: String) = s"$mmapi$suffix"


  //\\ implicits //\\

  // use inscope ActorSystem dispatcher
  implicit def sys2ec(implicit system: ActorSystem): ExecutionContext = system.dispatcher
}
