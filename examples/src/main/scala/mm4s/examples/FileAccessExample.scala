package mm4s.examples

import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import mm4s.api.FileModels.{FileInfo, FilePath}
import mm4s.api.FileProtocols._
import mm4s.api.Streams._
import mm4s.api.{Filez, _}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 *
 */
object FileAccessExample extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  val conn = connection("localhost")

  val path = FilePath("7sxiyug69fnqznzpmnaniimd8h", "yo9x1pa5iinejpowztnj7ee9or", "/98m85p6gcfbe3pn6raqkub8tta/docker-daemon.txt")
  Filez.get(path, "1cbcwtnsytbiuykiwh848jopoo").via(conn).runWith(Sink.foreach(println))
  Filez.info(path, "1cbcwtnsytbiuykiwh848jopoo").via(conn).mapAsync(1)(Unmarshal(_).to[FileInfo]).runWith(Sink.foreach(println))

  Await.ready(system.whenTerminated, Duration.Inf)

  def rand() = UUID.randomUUID.toString.substring(0, 5)
}
