package mm4s.dockerbot

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import com.typesafe.scalalogging.LazyLogging
import mm4s.api.Streams._
import mm4s.api.UserModels.LoginByUsername
import mm4s.api.Users
import mm4s.bots.Mattermost
import mm4s.bots.api.Connected

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 * Bootstrap a MM Bot
 */
object Boot extends App with LazyLogging {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  val host = sys.env.getOrElse("MM_HOST", "mattermost")
  val user = sys.env.getOrElse("BOT_USER", "bot")
  val pass = sys.env.getOrElse("BOT_PASS", "password")
  val team = sys.env.getOrElse("BOT_TEAM", "mmteam")
  val chan = sys.env.getOrElse("BOT_CHANNEL", "komb3qpj1pn4zytpkgrypsnwda")

  val conn = connection(host)

  // spi!
  //val impl = injectActor[DockerBot]

  val mm = Mattermost(chan)
  val done = Connected(mm)

  Users.login(LoginByUsername(user, pass, team))
  .via(conn)
  .via(Users.extractSession())
  .runWith(Sink.actorRef(Mattermost(chan), done))

  Await.ready(system.whenTerminated, Duration.Inf)
}
