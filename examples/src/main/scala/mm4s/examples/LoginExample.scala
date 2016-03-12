package mm4s.examples

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import mm4s.api.MessageModels.CreatePost
import mm4s.api.Streams._
import mm4s.api.UserModels.{LoggedIn, LoginByUsername}
import mm4s.api.WebSockets.SocketClosed
import mm4s.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 * relys on a standard mm configuration of
 * - team -> mmteam
 * - user -> root@mm.com
 * - pass -> password
 */
object LoginExample extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  val conn = connection("localhost")
  val logindata = LoginByUsername("root", "password", "mmteam")

  val bot = Bot("komb3qpj1pn4zytpkgrypsnwda")

  Users.login(logindata)
  .via(conn)
  .via(Users.extractSession())
  .runWith(Sink.actorRef(bot, LoginStreamClosed))

  Await.ready(system.whenTerminated, Duration.Inf)

  def rand() = UUID.randomUUID.toString.substring(0, 5)
}

case object LoginStreamClosed

object Bot {
  def apply(channel: String)(implicit system: ActorSystem, mat: ActorMaterializer) = {
    system.actorOf(Props(new Bot(channel)))
  }
}

class Bot(channel: String)(implicit mat: ActorMaterializer) extends Actor with ActorLogging {
  import context.system
  val conn = connection("localhost")

  def receive: Receive = {
    case m: LoggedIn =>
      log.debug(s"Bot ${m.details.username} Logged In, $m")
      Messages.create(CreatePost("I just logged in!", channel), m.token)
      .via(conn).runForeach(println)
  }
}
