package mm4s.bots

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.stream.ActorMaterializer
import mm4s.api.MessageModels.CreatePost
import mm4s.api.Messages
import mm4s.api.Streams._
import mm4s.api.UserModels.LoggedIn


object Mattermost {
  def apply(channel: String)(implicit system: ActorSystem, mat: ActorMaterializer) = {
    system.actorOf(Props(new Mattermost(channel)))
  }
}

class Mattermost(channel: String)(implicit mat: ActorMaterializer) extends Actor with ActorLogging {
  import context.system
  val conn = connection("localhost")

  def receive: Receive = {
    case m: LoggedIn =>
      log.debug(s"Bot ${m.details.username} Logged In, $m")
      Messages.create(CreatePost("I just logged in!", channel), m.token)
      .via(conn).runForeach(println)
  }
}
