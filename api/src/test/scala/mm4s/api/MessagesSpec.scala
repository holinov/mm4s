package mm4s.api

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import akka.testkit.TestKit
import mm4s.api.ChannelModels.ChannelListing
import mm4s.api.MessageModels.{CreatePost, PostQueryResponse, Posting}
import mm4s.api.MessageProtocols._
import mm4s.api.Streams._
import mm4s.test.IntegrationTest
import org.scalactic.FutureSugar
import org.scalatest.{AsyncWordSpecLike, Inside, Matchers}

import scala.concurrent.Await


class MessagesSpec extends TestKit(ActorSystem("MessagesSpec"))
                           with AsyncWordSpecLike with FutureSugar
                           with Matchers with Inside with IntegrationTest {

  implicit val mat = ActorMaterializer()

  val session = Await.result(token(), defaultDuration)
  val userid = session.details.id
  val channelid = Await.result(channel(), defaultDuration)

  "message creation" should {
    "create message" in {
      val msg = UUID.randomUUID().toString
      Messages.create(CreatePost(msg, channelid, Seq()), session.token).via(connection).via(response[Posting]).map { r =>
        r.message shouldBe msg
      }
      .runWith(Sink.head)
    }
  }

  "message fetching" should {
    "fetch messages since this test started" in {
      val f = Messages.create(CreatePost(UUID.randomUUID().toString, channelid, Seq()), session.token)
              .via(connection)
              .via(response[Posting])
              .runWith(Sink.head)
      val posting = Await.result(f, defaultDuration)

      val from = posting.create_at - 1
      Messages.since(from, channelid, session.token).via(connection()).via(response[PostQueryResponse]).map(_.posts).map {
        case Some(posts) =>
          posts.values should contain(posting)
        case _ =>
          fail("expected to find a post")
      }.runWith(Sink.head)
    }

    "not return messages from the future" in {
      Messages.since(System.currentTimeMillis() + 1000000, channelid, session.token).via(connection()).via(response[PostQueryResponse]).map(_.posts).map {
        case Some(posts) =>
          fail("expected there to be no posts")
        case _ =>
          succeed
      }.runWith(Sink.head)
    }
  }

  def channel() = {
    import ChannelProtocols._

    Channels.list(session.token)
    .via(connection())
    .via(response[ChannelListing])
    .map(l => l.channels.seq.head.id)
    .runWith(Sink.head)
  }
}
