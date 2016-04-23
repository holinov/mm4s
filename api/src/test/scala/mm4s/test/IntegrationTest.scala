package mm4s.test

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import akka.util.Timeout
import mm4s.api.UserModels.{LoggedIn, LoginByUsername}
import mm4s.api.{Streams, Users}
import org.scalatest.Tag

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt


trait IntegrationTest {
  // overridable access, defaults to admin
  def user: String = "admin"
  def pass: String = "password"
  def team: String = "humans"

  val defaultDuration = 10.seconds
  val defaultTimeout = Timeout(defaultDuration)

  def token()(implicit system: ActorSystem, mat: ActorMaterializer): Future[LoggedIn] = {
    Users.login(LoginByUsername(user, pass, team))
    .via(connection())
    .via(Users.extractSession())
    .runWith(Sink.head)
  }

  def connection()(implicit system: ActorSystem) = {
    Streams.connection("localhost", 8080)
  }

  def random() = UUID.randomUUID.toString.take(5)

  val integration = Tag("mm4s.test.IntegrationTest")
}
