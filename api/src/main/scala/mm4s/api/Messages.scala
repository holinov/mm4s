package mm4s.api

import java.nio.file.Path

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.stream.scaladsl.Source
import spray.json.{DefaultJsonProtocol, RootJsonFormat}


/**
 * Message to be posted
 */
case class Post(text: String)

case class PostWithChannel(text: String, channelId: String)

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

  def since(time: Long, channel_id: String, token: String)(implicit system: ActorSystem): Source[HttpRequest, NotUsed] = {
    get(s"/channels/$channel_id/posts/$time").map(withAuth(token))
  }
}

object MessageModels {
  case class CreatePost(message: String, channel_id: String, filenames: Seq[String] = Seq.empty)
  case class Posting(user_id: String, channel_id: String, message: String, hashtags: String, create_at: Long, filenames: Seq[String])
  case class PostQueryResponse(order: Seq[String], posts: Option[Map[String, Posting]])
}

object MessageProtocols extends DefaultJsonProtocol with SprayJsonSupport {
  import MessageModels._

  implicit val CreatePostFormat: RootJsonFormat[CreatePost] = jsonFormat3(CreatePost)
  implicit val PostingFormat: RootJsonFormat[Posting] = jsonFormat6(Posting)
  implicit val PostQueryResponseFormat: RootJsonFormat[PostQueryResponse] = jsonFormat2(PostQueryResponse)
}
