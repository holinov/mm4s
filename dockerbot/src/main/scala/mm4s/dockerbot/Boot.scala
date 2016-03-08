package mm4s.dockerbot

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpResponse, HttpRequest}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink}
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

  val conn: Flow[HttpRequest, HttpResponse, _] = connection(host)

  //val bot = injectActor[DockerBot]

  val api = Mattermost(chan)
  val done = Connected(api /* <-- bot */ , conn /* hack;; conn here for now  */)

  Users.login(LoginByUsername(user, pass, team))
  .via(conn)
  .via(Users.extractSession())
  .runWith(Sink.actorRef(api, done))

  Await.ready(system.whenTerminated, Duration.Inf)
}
