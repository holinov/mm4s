package mm4s.api

import akka.actor.ActorSystem
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import akka.testkit.TestKit
import mm4s.api.Streams._
import mm4s.api.TeamModels.{CreateTeam, Team, TeamCreated}
import mm4s.test.IntegrationTest
import org.scalatest.{Entry, Inside, AsyncWordSpecLike, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

/**
 * validate the create and list teams api
 */
class CreateAndListTeamsSpec extends TestKit(ActorSystem()) with AsyncWordSpecLike with Matchers with IntegrationTest with Inside {
  implicit val materializer = ActorMaterializer()
  import system.dispatcher

  val name = random()
  val email = s"a${random()}@bar.com"

  val session = Await.result(token(), 10.seconds)

  s"The $name team" should {
    import mm4s.api.TeamProtocols._

    "not exist" taggedAs integration in {
      Teams.list(session.token).via(connection()).mapAsync(1)(r => Unmarshal(r).to[Map[String, Team]])
      .map(_.values.map(_.name)).map(_ shouldNot contain(name))
      .runWith(Sink.head)
    }

    "be created" taggedAs integration in {
      Teams.create(CreateTeam(name, name, email))
      .via(connection())
      .via(response[TeamCreated])
      .runWith(Sink.head).map { res =>
        res.name shouldBe name
      }
    }

    "exist" taggedAs integration in {
      Teams.list(session.token).via(connection()).mapAsync(1)(r => Unmarshal(r).to[Map[String, Team]])
      .map(_.values.map(_.name)).map(_ should contain(name))
      .runWith(Sink.head)
    }
  }
}
