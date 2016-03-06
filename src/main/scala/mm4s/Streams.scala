package mm4s

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse, MessageEntity}
import akka.http.scaladsl.unmarshalling.{Unmarshal, Unmarshaller}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Source}

import scala.concurrent.Future

/**
 * Various reusable stream components
 */
object Streams {

  /**
   * Create a HttpRequest as a Source
   */
  def request(path: String)(fe: => Future[MessageEntity])(implicit system: ActorSystem) = {
    Source.fromFuture(fe).map(e =>
      HttpRequest(uri = s"$mmapi$path", entity = e, method = HttpMethods.POST)
    )
  }

  /**
   * create a http connection to the host and port as a Flow[HttpRequest, HttpResponse, ]
   */
  def connection(host: String, port: Int = 8080)(implicit system: ActorSystem) = {
    Http().outgoingConnection(host, port)
  }

  /**
   * extract a [[HttpResponse]] from the Stream
   */
  def response[T](implicit system: ActorSystem, mat: ActorMaterializer, um: Unmarshaller[HttpResponse, T]) = {
    Flow[HttpResponse].mapAsync(1)(r => Unmarshal(r).to[T])
  }
}
