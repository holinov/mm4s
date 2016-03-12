package mm4s.api

import akka.actor.{ActorSystem, ActorRef}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.Cookie
import akka.http.scaladsl.model.ws.{Message, WebSocketRequest, TextMessage}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl.{Flow, Keep, Source, Sink}

/**
 *
 */
object WebSockets {

  def flow(token: String, host: String, port: Int = 8080)(implicit system: ActorSystem) = {
    val mmheader = scala.collection.immutable.Seq(Cookie("MMTOKEN", token))
    Http().webSocketClientFlow(WebSocketRequest(s"ws://$host:$port$mmapi/websocket?session_token_index=0", extraHeaders = mmheader))
  }

  def source()(implicit system: ActorSystem) = {
    Source.queue[TextMessage](10, OverflowStrategy.fail)
  }

  def toActor(ref: ActorRef)(implicit system: ActorSystem) = {
    Sink.actorRef(ref, akka.Done)
  }

  def connect(ref: ActorRef, token: String, host: String, port: Int = 8080)(implicit system: ActorSystem, mat: ActorMaterializer) = {
    source()
    .viaMat(flow(token, host, port))(Keep.both)
    .via(Flow[Message].collect { case m: TextMessage.Strict => m.text })
    .toMat(toActor(ref))(Keep.both)
    .run()
  }
}
