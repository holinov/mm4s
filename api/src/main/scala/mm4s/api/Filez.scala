package mm4s.api

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.Cookie
import mm4s.api.Streams._
import spray.json._

/**
 *
 */
object Filez {
  import FileModels._
  import FileProtocols._

  def get(path: FilePath, token: String)(implicit system: ActorSystem) = {
    request(s"/files/get/${fileurl(path)}") { r =>
      Marshal(path).to[MessageEntity].map(r.withHeaders(Cookie("MMTOKEN", token)).withEntity)
    }
  }

  def info(path: FilePath, token: String)(implicit system: ActorSystem) = {
    request(s"/files/get_info/${fileurl(path)}") { r =>
      Marshal(path).to[MessageEntity].map(r.withHeaders(Cookie("MMTOKEN", token)).withEntity)
    }
  }

  def fileurl(path: FilePath): String = s"${path.channel_id}/${path.user_id}${path.filename}"
}

object FileModels {
  case class FilePath(channel_id: String, user_id: String, filename: String)
  case class FileInfo(filename: String, size: Int, extension: String, mime_type: String, has_preview_image: Boolean)
}

object FileProtocols extends DefaultJsonProtocol with SprayJsonSupport {
  import FileModels._

  implicit val FileInfoFormat: RootJsonFormat[FileInfo] = jsonFormat5(FileInfo)
  implicit val FilePathFormat: RootJsonFormat[FilePath] = jsonFormat3(FilePath)
}
