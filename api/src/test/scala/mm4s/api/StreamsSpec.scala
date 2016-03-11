package mm4s.api

import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpEntity, HttpMethods, HttpRequest}
import akka.stream.ActorMaterializer
import akka.stream.testkit.scaladsl.TestSink
import akka.testkit.TestKit
import org.scalatest.{Matchers, WordSpecLike}

import scala.concurrent.Future

/**
 *
 */
class StreamsSpec extends TestKit(ActorSystem("StreamsSpec"))
                          with WordSpecLike with Matchers {

  implicit val mat = ActorMaterializer()

  "request factory" should {
    val path = s"/${UUID.randomUUID.toString.take(5)}"
    val expected = HttpRequest(uri = uripath(path))

    "provide empty GET" when {
      val expectedEmpty = expected.withEntity(HttpEntity.Empty)

      "GET factory called" in {
        Streams.get(path)
        .runWith(TestSink.probe[HttpRequest])
        .request(1)
        .expectNext(expectedEmpty)
        .expectComplete()
      }

      "request defaults to GET" in {
        Streams.request(path)(Future.successful)
        .runWith(TestSink.probe[HttpRequest])
        .request(1)
        .expectNext(expectedEmpty)
        .expectComplete()
      }
    }

    "provide modified request" when {
      "request set to POST" in {
        Streams.request(path)(r => Future.successful(r.withMethod(HttpMethods.POST)))
        .runWith(TestSink.probe[HttpRequest])
        .request(1)
        .expectNext(expected.withMethod(HttpMethods.POST))
        .expectComplete()
      }
    }
  }
}
