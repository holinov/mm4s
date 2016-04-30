package mm4s.bots

import akka.actor._
import akka.http.scaladsl.model.HttpRequest
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink}
import com.rxthings.di._
import mm4s.api.MessageModels.CreatePost
import mm4s.api.UserModels.{LoggedIn, LoggedInToChannel, Session}
import mm4s.api.WebSocketModels.WebSocketMessage
import mm4s.api._
import mm4s.bots.api.ConfigKeys._
import mm4s.bots.api._

object Gateway {
  def apply(flow: Connection)(implicit system: ActorSystem, mat: ActorMaterializer) = {
    system.actorOf(Props(new Gateway(flow)))
  }
}

class Gateway(flow: Connection)(implicit mat: ActorMaterializer) extends Actor with ActorLogging {
  val mmhost: String = inject[String] annotated key.host
  val mmport: Int = inject[Int] annotated key.port

  def receive: Receive = {
    case l: LoggedIn =>
      log.warning("default channel was not found, must be specified on all messages")
      context.become(loggedIn(l, None))

    case l: LoggedInToChannel =>
      context.become(loggedIn(l, Option(l.channelId)))
  }

  def loggedIn(s: Session, defChannel: Option[String]): Receive = {
    WebSockets.connect(self, s.token, mmhost, mmport)
    log.debug("Bot {} Logged In [{}]", s.details.username, s)

    {
      case r: Register =>
        context.become(registered(r.bot, s, defChannel))
    }
  }

  def registered(bot: ActorRef, session: Session, defChannel: Option[String]): Receive = {
    log.debug("[{}] has registered", session.details.username)
    bot ! Ready(self, BotID(session.details.username))

    {
      case Post(t) => defChannel.foreach { c =>
        Messages.create(CreatePost(t, c), session.token).via(flow).runWith(Sink.ignore)
      }

      case PostWithChannel(t, c) =>
        Messages.create(CreatePost(t, c), session.token).via(flow).runWith(Sink.ignore)

      case wsm: WebSocketMessage =>
        wsm.props.posted.foreach(p => bot ! Posted(p.message))

      // prototyping usage of authenticated connection #33
      case ConnectionRequest() =>
        val conn: Connection = Flow[HttpRequest].map(withAuth(session.token)).via(flow)
        sender ! conn
    }
  }
}
