package examples

import java.util.UUID

import akka.actor.Actor.Receive
import akka.actor.{Props, ActorLogging, Actor, ActorSystem}
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.headers.`Set-Cookie`
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Flow}
import mm4s.MessageModels.CreatePost
import mm4s.Streams._
import mm4s.UserModels.{LoggedIn, LoginByEmail}
import mm4s.{Messages, Users}

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
  val logindata = LoginByEmail("root@mm.com", "password", "mmteam")

  val bot = Bot()

  Users.login(logindata)
  .via(conn)
  .via(Users.extractSession())
  .runWith(Sink.actorRef(bot, Done()))

  Await.ready(system.whenTerminated, Duration.Inf)

  def rand() = UUID.randomUUID.toString.substring(0, 5)
}

case class Done()

object Bot {
  def apply()(implicit system: ActorSystem, mat: ActorMaterializer) = {
    system.actorOf(Props(new Bot()))
  }
}

class Bot()(implicit mat: ActorMaterializer) extends Actor with ActorLogging {
  import context.system
  val conn = connection("localhost")

  def receive: Receive = {
    case m: LoggedIn =>
      log.debug(s"Bot ${m.details.username} Logged In, $m")
      Messages.create(CreatePost("I just logged in!", "komb3qpj1pn4zytpkgrypsnwda"), m.token)
      .via(conn).runForeach(println)
  }
}
