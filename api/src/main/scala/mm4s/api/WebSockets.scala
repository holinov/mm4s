package mm4s.api

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.Cookie
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketRequest}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import mm4s.api.WebSocketModels.WebSocketMessage
import spray.json._


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
    import WebSocketProtocol._

    source()
    .viaMat(flow(token, host, port))(Keep.both)
    .via(Flow[Message].collect { case m: TextMessage.Strict => m.text })
    .map(s => s.parseJson.convertTo[WebSocketMessage])
    .toMat(toActor(ref))(Keep.both)
    .run()
  }
}


object WebSocketModels {
  case class WebSocketMessage(team_id: String, channel_id: String, user_id: String, action: Action, props: WsmProps)
  case class WsmPost(id: String, create_at: Long, user_id: String, channel_id: String, message: String, `type`: String, hashtags: String, filenames: Seq[String])
  case class WsmProps(channel_type: Option[String], otherFile: Option[String], post: Option[String]) {
    import WebSocketProtocol.wsmPostFormat
    def posted: Option[WsmPost] = post.map(_.parseJson.convertTo[WsmPost])
  }
}


object WebSocketProtocol extends DefaultJsonProtocol {
  import ActionProtocol._
  import WebSocketModels._

  implicit val wsmPostFormat: RootJsonFormat[WsmPost] = jsonFormat8(WsmPost)
  implicit val wsmPropsFormat: RootJsonFormat[WsmProps] = jsonFormat3(WsmProps)
  implicit val wsmFormat: RootJsonFormat[WebSocketMessage] = jsonFormat5(WebSocketMessage)
}
