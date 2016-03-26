package mm4s.api

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.headers.Cookie
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

object Channels {
  import Streams._

  def list(token: String)(implicit system: ActorSystem, mat: ActorMaterializer): Source[HttpRequest, NotUsed] = {
    get("/channels/").map(r => r.withHeaders(Cookie("MMTOKEN", token)))
  }
}

object ChannelModels {
  case class Channel(id: String, name: String, displayName: String)
  case class ChannelMember(channelId: String, userId: String)
  case class ChannelListing(channels: Seq[Channel], members: Map[String, ChannelMember])
}

object ChannelProtocols extends DefaultJsonProtocol {
  import ChannelModels._

  implicit val channelFormat: RootJsonFormat[Channel] = jsonFormat(Channel, "id", "name", "display_name")
  implicit val channelMemberFormat: RootJsonFormat[ChannelMember] = jsonFormat(ChannelMember, "channel_id", "user_id")
  implicit val channelListingFormat: RootJsonFormat[ChannelListing] = jsonFormat2(ChannelListing)
}
