package mm4s.api

import java.nio.file.Path

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import spray.json.{DefaultJsonProtocol, RootJsonFormat}


/**
 * Message to be posted
 */
case class Post(text: String)

case class PostWithAttachment(text: String, path: Path)

/**
 * Indicates a message was posted
 */
case class Posted(text: String)

/**
 *
 */
object Messages {
  import MessageModels._
  import MessageProtocols._
  import Streams._

  def create(post: CreatePost, token: String)(implicit system: ActorSystem) = {
    request(s"/channels/${post.channel_id}/create") { r =>
      Marshal(post).to[MessageEntity].map(r.withMethod(HttpMethods.POST).withHeaders(auth(token)).withEntity)
    }
  }
}

object MessageModels {
  case class CreatePost(message: String, channel_id: String, filenames: Seq[String] = Seq.empty)
}

object MessageProtocols extends DefaultJsonProtocol with SprayJsonSupport {
  import MessageModels._

  implicit val CreatePostFormat: RootJsonFormat[CreatePost] = jsonFormat3(CreatePost)
}
