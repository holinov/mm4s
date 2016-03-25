package mm4s.api

import akka.actor.ActorSystem
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import akka.testkit.TestKit
import mm4s.api.Streams._
import mm4s.api.TeamModels.Team
import mm4s.api.UserModels.{CreateUser, User, UserCreated}
import mm4s.test.IntegrationTest
import org.scalatest.{AsyncWordSpecLike, Inside, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, DurationInt}


class CreateAndListUsersSpec extends TestKit(ActorSystem()) with AsyncWordSpecLike with Matchers with Inside with IntegrationTest {
  implicit val materializer = ActorMaterializer()
  import TeamProtocols._
  import system.dispatcher

  val username = s"a${random()}"
  val password = "password"
  val email = s"$username@bar.com"

  val session = Await.result(token(), 10.seconds)

  val teamId = Await.result(Teams.list(session.token)
                            .via(connection())
                            .via(Streams.response[Map[String, Team]])
                            .map(_.values.collectFirst { case Team(id, n) if n == team => id })
                            .runWith(Sink.head), Duration.Inf).get

  s"User $username" should {
    import UserProtocols._

    "not exist" taggedAs integration in {
      Users.list(team, session.token).via(connection()).mapAsync(1)(r => Unmarshal(r).to[Map[String, User]])
      .map(_.values.map(_.username)).map(_ shouldNot contain(username))
      .runWith(Sink.head)
    }
    "be created" taggedAs integration in {
      Users.create(CreateUser(username, password, email, teamId))
      .via(connection())
      .via(response[UserCreated])
      .runWith(Sink.head).map { res =>
        res.username shouldBe username
      }
    }
    "exist" taggedAs integration in {
      Users.list(team, session.token).via(connection()).mapAsync(1)(r => Unmarshal(r).to[Map[String, User]])
      .map(_.values.map(_.username)).map(_ should contain(username))
      .runWith(Sink.head)
    }
  }
}
