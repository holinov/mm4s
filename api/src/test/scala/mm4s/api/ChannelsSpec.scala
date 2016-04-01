package mm4s.api

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import akka.testkit.TestKit
import mm4s.api.ChannelModels.ChannelListing
import mm4s.api.ChannelProtocols._
import mm4s.api.Streams._
import mm4s.test.IntegrationTest
import org.scalatest.{AsyncWordSpecLike, Inside, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class ChannelsSpec extends TestKit(ActorSystem()) with AsyncWordSpecLike with Matchers with Inside with IntegrationTest {
  implicit val materializer = ActorMaterializer()

  val session = Await.result(token(), 10.seconds)

  "default channels" should {
    "list" taggedAs integration in {
      Channels.list(session.token).via(connection()).via(response[ChannelListing]).map { l =>
        l.channels.map(_.displayName) should contain allOf("Town Square", "Off-Topic")
      }.runWith(Sink.head)
    }

    "find using name" taggedAs integration in {
      Channels.list(session.token).via(connection()).via(Channels.findany("town-square")).map { c =>
        c shouldBe defined
        c.get.name shouldBe "town-square"
      }.runWith(Sink.head)
    }

    "find using display name" taggedAs integration in {
      Channels.list(session.token).via(connection()).via(Channels.findany("Town Square")).map { c =>
        c shouldBe defined
        c.get.displayName shouldBe "Town Square"
      }.runWith(Sink.head)
    }

    "find using id" taggedAs integration in {
      val f = Channels.list(session.token)
              .via(connection())
              .via(response[ChannelListing])
              .map(_.channels.head)
              .map(c => c.id -> c.name)
              .runWith(Sink.head)
      val idname = Await.result(f, 10.seconds)

      Channels.list(session.token).via(connection()).via(Channels.findany(idname._1)).map { c =>
        c shouldBe defined
        c.get.id shouldBe idname._1
        c.get.name shouldBe idname._2
      }.runWith(Sink.head)
    }
  }
}
