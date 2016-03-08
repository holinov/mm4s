package mm4s.bots

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink}
import mm4s.api.MessageModels.CreatePost
import mm4s.api.Messages
import mm4s.api.UserModels.LoggedIn
import mm4s.bots.api.Connected


object Mattermost {
  def apply(channel: String)(implicit system: ActorSystem, mat: ActorMaterializer) = {
    system.actorOf(Props(new Mattermost(channel)))
  }
}

class Mattermost(channel: String)(implicit mat: ActorMaterializer) extends Actor with ActorLogging {
  import context.system
  var login: Option[LoggedIn] = None
  var conn: Option[Flow[HttpRequest, HttpResponse, _]] = None

  def receive: Receive = {
    case Connected(api, c) => conn = Option(c)
      login.zip(conn).foreach { c =>
        val token = c._1.token
        val user = c._1.details.username
        Messages.create(CreatePost(s"Dockerbot $user just logged in!", channel), token).via(c._2).runWith(Sink.ignore)
      }

    case m: LoggedIn =>
      login = Option(m)
      log.debug(s"Bot ${m.details.username} Logged In, $m")
  }
}
