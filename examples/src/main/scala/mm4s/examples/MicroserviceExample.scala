package mm4s.examples

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import mm4s.api.{UserModels, Users, Streams}
import Streams._
import UserModels.LoginByUsername

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 * A quick sketch to show what the bootstrap of a
 * microservice deployment of a mm4s bot would look like.
 *
 * Expectations would be containerized deployment and
 * most likely service discovery would be available through
 * Consul or similar.
 *
 */
object MicroserviceExample extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  val host = sys.env.getOrElse("MM_HOST", "localhost" /* "mattermost" */)
  val user = sys.env.getOrElse("BOT_USER", "root")
  val pass = sys.env.getOrElse("BOT_PASS", "password")
  val team = sys.env.getOrElse("BOT_TEAM", "mmteam")
  val chan = sys.env.getOrElse("BOT_CHANNEL", "komb3qpj1pn4zytpkgrypsnwda")

  val conn = connection(host)

  Users.login(LoginByUsername("root", pass, team))
  .via(conn)
  .via(Users.extractSession())
  .runWith(Sink.actorRef(Bot(chan), Done()))

  Await.ready(system.whenTerminated, Duration.Inf)

  def rand() = UUID.randomUUID.toString.substring(0, 5)
}
