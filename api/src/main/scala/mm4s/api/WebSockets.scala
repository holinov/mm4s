package mm4s.api

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.Cookie
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketRequest}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}

/**
 *
 */
object WebSockets {
  case object SocketClosed

  def flow(token: String, host: String, port: Int = 8080)(implicit system: ActorSystem) = {
    val mmheader = scala.collection.immutable.Seq(Cookie("MMTOKEN", token))
    Http().webSocketClientFlow(WebSocketRequest(s"ws://$host:$port$mmapi/websocket", extraHeaders = mmheader))
  }

  def source()(implicit system: ActorSystem) = {
    Source.queue[TextMessage](10, OverflowStrategy.fail)
  }

  def toActor(ref: ActorRef)(implicit system: ActorSystem) = {
    Sink.actorRef(ref, SocketClosed)
  }

  def connect(ref: ActorRef, token: String, host: String, port: Int = 8080)(implicit system: ActorSystem, mat: ActorMaterializer) = {
    source()
    .viaMat(flow(token, host, port))(Keep.both)
    .via(Flow[Message].collect { case m: TextMessage.Strict => Posted(m.text) })
    .toMat(toActor(ref))(Keep.both)
    .run()
  }
}
