package mm4s.bots

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink}
import com.rxthings.di._
import mm4s.api.MessageModels.CreatePost
import mm4s.api.UserModels.LoggedIn
import mm4s.api.{Messages, WebSockets}
import mm4s.bots.api.{BotID, Connected, Message, Ready}

object Mattermost {
  def apply(channel: String)(implicit system: ActorSystem, mat: ActorMaterializer) = {
    system.actorOf(Props(new Mattermost(channel)))
  }
}

class Mattermost(channel: String)(implicit mat: ActorMaterializer) extends Actor with ActorLogging {
  var login: Option[LoggedIn] = None
  var conn: Option[Flow[HttpRequest, HttpResponse, _]] = None

  val mmhost: String = inject[String] annotated "mm.host"
  val mmport: String = inject[String] annotated "mm.port"

  def receive: Receive = {
    case Connected(bot, c) =>
      conn = Option(c)
      val username = login.get /* hack */ .details.username
      log.debug("[{}] has connected", username)
      bot ! Ready(self, BotID(username))

    case m: LoggedIn =>
      login = Option(m)
      WebSockets.connect(self, m.token, mmhost, mmport.toInt /* hack;; #8 */)
      log.debug(s"Bot ${m.details.username} Logged In, $m")

    case Message(t) =>
      login.zip(conn).foreach { c =>
        val token = c._1.token
        Messages.create(CreatePost(t, channel), token).via(c._2).runWith(Sink.ignore)
      }

    case m: String =>
      log.debug("incoming: [{}]", m)
  }
}
