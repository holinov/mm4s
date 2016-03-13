package mm4s.api

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.{Unmarshal, Unmarshaller}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Source}

import scala.concurrent.Future

/**
 * Various reusable stream components
 */
object Streams {
  type RequestBuilder = HttpRequest => Future[HttpRequest]

  /**
   * Create a GET HttpRequest as a Source
   */
  def get(path: String)(implicit system: ActorSystem): Source[HttpRequest, NotUsed] = {
    request(path)(Future.successful)
  }

  /**
   * Create a HttpRequest as a Source with access to modify its construction
   */
  def request(path: String)(builder: RequestBuilder)(implicit system: ActorSystem): Source[HttpRequest, NotUsed] = {
    Source.fromFuture(builder(HttpRequest(uri = uripath(path))))
  }

  /**
   * create a http connection to the host and port as a Flow[HttpRequest, HttpResponse, ]
   */
  def connection(host: String, port: Int = 8080)(implicit system: ActorSystem): ApiFlow = {
    Http().outgoingConnection(host, port)
  }

  /**
   * extract a [[HttpResponse]] from the Stream
   */
  def response[T](implicit system: ActorSystem, mat: ActorMaterializer, um: Unmarshaller[HttpResponse, T]) = {
    Flow[HttpResponse].mapAsync(1)(r => Unmarshal(r).to[T])
  }
}
