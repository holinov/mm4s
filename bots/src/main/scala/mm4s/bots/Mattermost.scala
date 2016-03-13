package mm4s.bots

import akka.actor._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import com.rxthings.di._
import mm4s.api.MessageModels.CreatePost
import mm4s.api.UserModels.LoggedIn
import mm4s.api._
import mm4s.bots.api._

object Mattermost {
  def apply(channel: String, flow: ApiFlow)(implicit system: ActorSystem, mat: ActorMaterializer) = {
    system.actorOf(Props(new Mattermost(channel, flow)))
  }
}

class Mattermost(channel: String, flow: ApiFlow)(implicit mat: ActorMaterializer) extends Actor with ActorLogging {
  val mmhost: String = inject[String] annotated "mm.host"
  val mmport: Int = inject[Int] annotated "mm.port"

  def receive: Receive = {
    case l: LoggedIn =>
      context.become(loggedin(l))
  }

  def loggedin(l: LoggedIn): Receive = {
    WebSockets.connect(self, l.token, mmhost, mmport.toInt /* hack;; #8 */)
    log.debug(s"Bot ${l.details.username} Logged In, $l")

    {
      case r: Register =>
        context.become(registered(r, l))
    }
  }

  def registered(r: Register, l: LoggedIn): Receive = {
    log.debug("[{}] has registered", l.details.username)

    {
      case Post(t) =>
        Messages.create(CreatePost(t, channel), l.token).via(flow).runWith(Sink.ignore)

      case m: Posted =>
        r.bot ! m
    }
  }
}
