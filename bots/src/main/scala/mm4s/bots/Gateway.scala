package mm4s.bots

import akka.actor._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import com.rxthings.di._
import mm4s.api.FileModels.{FilesUploaded, FileUpload}
import mm4s.api.MessageModels.CreatePost
import mm4s.api.UserModels.{LoggedInToChannel, LoggedIn}
import mm4s.api.WebSocketModels.WebSocketMessage
import mm4s.api._
import mm4s.bots.api.ConfigKeys._
import mm4s.bots.api._
import FileProtocols._
import Streams._

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
      log.warning("channel was not selected, gateway not activated")

    case l: LoggedInToChannel =>
      context.become(loggedin(l))
  }

  def loggedin(l: LoggedInToChannel): Receive = {
    WebSockets.connect(self, l.token, mmhost, mmport.toInt /* hack;; #8 */)
    log.debug(s"Bot ${l.details.username} Logged In, $l")

    {
      case r: Register =>
        context.become(registered(r, l))
    }
  }

  def registered(r: Register, l: LoggedInToChannel): Receive = {
    log.debug("[{}] has registered", l.details.username)
    r.bot ! Ready(self, BotID(l.details.username))

    {
      case Post(t) =>
        Messages.create(CreatePost(t, l.channelId), l.token).via(flow).runWith(Sink.ignore)

      case PostWithAttachment(t, p) =>
        Filez.put(FileUpload(l.channelId, p.toFile), l.token)
        .via(flow)
        .via(response[FilesUploaded])
        .mapAsync(1)(f => Messages.create(CreatePost(t, l.channelId, f.filenames), l.token).via(flow).runWith(Sink.head))
        .runWith(Sink.ignore)

      case wsm: WebSocketMessage =>
        wsm.props.posted.foreach(p => r.bot ! Posted(p.message))
    }
  }
}
