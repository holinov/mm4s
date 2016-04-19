package mm4s.api

import java.io.File

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.stream.scaladsl.{FileIO, Source}
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
      Marshal(path).to[MessageEntity].map(r.withHeaders(auth(token)).withEntity)
    }
  }


  def put(fu: FileUpload, token: String)(implicit system: ActorSystem) = {
    val file = fu.file
    val formData = Multipart.FormData(
      Source(List(
        Multipart.FormData.BodyPart(
          "files",
          HttpEntity(ContentTypes.`text/plain(UTF-8)`, file.length(), FileIO.fromFile(file, chunkSize = 100000)),
          Map("filename" -> fu.file.getName)
        ),
        Multipart.FormData.BodyPart("channel_id", fu.channelId)
      ))
    )

    request(s"/files/upload") { r =>
      Marshal(formData).to[RequestEntity].map(r.withHeaders(auth(token)).withMethod(HttpMethods.POST).withEntity)
    }
  }

  def info(path: FilePath, token: String)(implicit system: ActorSystem) = {
    request(s"/files/get_info/${fileurl(path)}") { r =>
      Marshal(path).to[MessageEntity].map(r.withHeaders(auth(token)).withEntity)
    }
  }

  def fileurl(path: FilePath): String = s"${path.channel_id}/${path.user_id}/${path.filename}"
}

object FileModels {
  case class FileUpload(channelId: String, file: File)
  case class FilesUploaded(filenames: Seq[String])

  case class FilePath(channel_id: String, user_id: String, filename: String)
  case class FileInfo(filename: String, size: Int, extension: String, mime_type: String, has_preview_image: Boolean)
}

object FileProtocols extends DefaultJsonProtocol with SprayJsonSupport {
  import FileModels._

  implicit val FilesUploadedFormat: RootJsonFormat[FilesUploaded] = jsonFormat1(FilesUploaded)
  implicit val FileInfoFormat: RootJsonFormat[FileInfo] = jsonFormat5(FileInfo)
  implicit val FilePathFormat: RootJsonFormat[FilePath] = jsonFormat3(FilePath)
}
