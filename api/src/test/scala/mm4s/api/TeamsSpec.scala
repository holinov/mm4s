package mm4s.api

import akka.actor.ActorSystem
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.headers.Cookie
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, RequestEntity}
import akka.stream.ActorMaterializer
import akka.stream.testkit.scaladsl.TestSink
import akka.testkit.TestKit
import mm4s.api.TeamModels.CreateTeam
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpecLike}

/**
 *
 */
class TeamsSpec extends TestKit(ActorSystem("TeamsSpec"))
                        with WordSpecLike with Matchers with ScalaFutures {

  implicit val mat = ActorMaterializer()

  "api calls" should {
    "have proper paths" when {
      "create" in {
        import TeamProtocols._
        val o = CreateTeam("", "", "")
        val e = Marshal(o).to[RequestEntity].futureValue

        val path = uripath("/teams/create")

        Teams.create(o)
        .runWith(TestSink.probe[HttpRequest])
        .request(1)
        .expectNext(HttpRequest(uri = path, method = POST, entity = e))
        .expectComplete()
      }
      "all" in {
        val path = uripath("/teams/all")

        Teams.list("token")
        .runWith(TestSink.probe[HttpRequest])
        .request(1)
        .expectNext(HttpRequest(uri = path, method = GET, headers = List(Cookie("MMTOKEN", "token"))))
        .expectComplete()
      }
    }
  }
}
