package mm4s.api

import java.nio.file.{Files, Paths}

import akka.actor.ActorSystem
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.testkit.TestKit
import mm4s.api.ChannelModels.ChannelListing
import mm4s.api.FileModels.{FilePath, FileUpload, FilesUploaded}
import mm4s.api.FileProtocols._
import mm4s.api.Streams._
import mm4s.test.IntegrationTest
import org.scalactic.FutureSugar
import org.scalatest.{AsyncWordSpecLike, Inside, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt


class FileAccessSpec extends TestKit(ActorSystem()) with AsyncWordSpecLike with FutureSugar with Matchers with Inside with IntegrationTest {
  implicit val materializer = ActorMaterializer()
  import system.dispatcher

  val session = Await.result(token(), 10.seconds)

  val fspath = tmp()
  println(s"temp file at $fspath")
  val content = s"some-random-content[${random()}]"

  Source.single(content).to(Sink.foreach(c => Files.write(fspath, c.getBytes))).run()

  val userid = session.details.id
  val channelid = Await.result(channel(), 10.seconds)


  "Files" should {
    "upload" taggedAs integration in {
      Filez.put(FileUpload(channelid, fspath.toFile), session.token).via(connection()).via(response[FilesUploaded]).map { r =>
        r.filenames.map(Paths.get(_).getFileName) should contain(fspath.getFileName)
      }.runWith(Sink.head)
    }

    "download" taggedAs integration in {
      val f1 = Filez.put(FileUpload(channelid, fspath.toFile), session.token).via(connection()).via(response[FilesUploaded]).map { r =>
        r.filenames.head.split("/")
      }.runWith(Sink.head)

      // split result into channel/user/pre/filename
      val up = Await.result(f1, 5.seconds).filter(_.nonEmpty)
      up.length shouldBe 4

      Filez.get(FilePath(up(0), up(1), s"${up(2)}/${up(3)}"), session.token)
      .via(connection())
      .mapAsync(1)(Unmarshal(_).to[String]).runWith(Sink.head)
      .map(_ shouldBe content)
    }
  }

  def tmp() = Paths.get(sys.props.getOrElse("java.io.tmpdir", "/tmp"), random())
  def channel() = {
    import ChannelProtocols._

    Channels.list(session.token)
    .via(connection())
    .via(response[ChannelListing])
    .map(l => l.channels.seq.head.id)
    .runWith(Sink.head)
  }
}
