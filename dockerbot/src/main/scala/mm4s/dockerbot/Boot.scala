package mm4s.dockerbot

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink}
import com.rxthings.di._
import com.typesafe.scalalogging.LazyLogging
import mm4s.api.Streams._
import mm4s.api.UserModels.LoginByUsername
import mm4s.api.Users
import mm4s.bots.Mattermost
import mm4s.bots.api.{Bot, Connected}
import net.ceedubs.ficus.Ficus._
import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 * Bootstrap a MM Bot
 */
object Boot extends App with LazyLogging {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  val config = system.settings.config

  import ConfigKeys._
  val host = resolve(env.host, key.host, mmHost)
  val user = resolve(env.user, key.user, mmUser)
  val pass = resolve(env.pass, key.pass, mmPass)
  val team = resolve(env.team, key.team, mmTeam)
  val chan = resolve(env.channel, key.channel, mmChannel)

  println(s"host:$host user:$user pass:$pass team:$team chan:$chan")

  val conn: Flow[HttpRequest, HttpResponse, _] = connection(host)

  val bot: ActorRef = injectActor[Bot]
  val done = Connected(bot, conn /* hack;; dont like conn here */)

  Users.login(LoginByUsername(user, pass, team))
  .via(conn)
  .via(Users.extractSession())
  .runWith(Sink.actorRef(Mattermost(chan), done))

  Await.ready(system.whenTerminated, Duration.Inf)

  def resolve(env: String, key: String, default: String) =
    sys.env.get(env).orElse(config.getAs[String](key)).getOrElse(default)
}
