package mm4s.api

import akka.actor.ActorSystem
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.{HttpRequest, RequestEntity}
import akka.stream.ActorMaterializer
import akka.stream.testkit.scaladsl.TestSink
import akka.testkit.TestKit
import mm4s.api.UserModels.{CreateUser, LoginByUsername}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpecLike}

/**
 *
 */
class UsersSpec extends TestKit(ActorSystem("UsersSpec"))
                        with WordSpecLike with Matchers with ScalaFutures {

  implicit val mat = ActorMaterializer()

  "api calls" should {
    "have proper paths" when {
      "create" in {
        import UserProtocols._
        val o = CreateUser("", "", "", "")
        val e = Marshal(o).to[RequestEntity].futureValue

        val path = uripath("/users/create")

        Users.create(o)
        .runWith(TestSink.probe[HttpRequest])
        .request(1)
        .expectNext(HttpRequest(uri = path, method = POST, entity = e))
        .expectComplete()
      }

      "login" in {
        import UserProtocols._
        val o = LoginByUsername("", "", "")
        val e = Marshal(o).to[RequestEntity].futureValue

        val path = uripath("/users/login")

        Users.login(o)
        .runWith(TestSink.probe[HttpRequest])
        .request(1)
        .expectNext(HttpRequest(uri = path, method = POST, entity = e))
        .expectComplete()
      }
    }
  }
}
