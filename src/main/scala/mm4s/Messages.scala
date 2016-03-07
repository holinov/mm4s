package mm4s

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.Cookie
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

/**
 *
 */
object Messages {
  import MessageModels._
  import MessageProtocols._
  import Streams._

  def create(post: CreatePost, token: String)(implicit system: ActorSystem) = {
    request(s"/channels/${post.channel_id}/create") { r =>
      Marshal(post).to[MessageEntity].map(r.withMethod(HttpMethods.POST).withHeaders(Cookie("MMTOKEN", token)).withEntity)
    }
  }
}

object MessageModels {
  case class CreatePost(message: String, channel_id: String)
}

object MessageProtocols extends DefaultJsonProtocol with SprayJsonSupport {
  import MessageModels._

  implicit val CreatePostFormat: RootJsonFormat[CreatePost] = jsonFormat2(CreatePost)
}
