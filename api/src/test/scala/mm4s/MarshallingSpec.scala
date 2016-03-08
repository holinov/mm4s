package mm4s

import akka.actor.ActorSystem
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.MessageEntity
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import mm4s.UserModels.CreateUser
import org.scalactic.FutureSugar
import org.scalatest._
import spray.json._

/**
 * https://github.com/akka/akka/tree/master/akka-http-tests/src/test/scala/akka/http/scaladsl
 */
class MarshallingSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll with FutureSugar {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  import system.dispatcher


  "user models" should {
    import UserProtocols._

    val createUserJson =
      """{
        |"username":"bob",
        |"password":"pass",
        |"email":"bob@bob.com",
        |"team_id":"team-id"
        |}""".stripMargin

    "CreateUser" in {
      val m = CreateUser("bob", "pass", "bob@bob.com", "team-id")
      Marshal(m).to[MessageEntity].flatMap(_.dataBytes.map(_.utf8String).runWith(Sink.head)).map { r =>
        r.parseJson shouldBe createUserJson.parseJson
      }
    }
  }

  override protected def afterAll(): Unit = system.terminate()
}
