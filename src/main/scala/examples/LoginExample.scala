package examples

import java.util.UUID

import akka.actor.Actor.Receive
import akka.actor.{Props, ActorLogging, Actor, ActorSystem}
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.headers.`Set-Cookie`
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Flow}
import mm4s.Streams._
import mm4s.UserModels.LoginByEmail
import mm4s.Users

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

  val bot = system.actorOf(Props[Bot])

  Users.login(logindata)
  .via(conn)
  .via(Users.extractSession())
  .runWith(Sink.actorRef(bot, Done()))

  Await.ready(system.whenTerminated, Duration.Inf)

  def rand() = UUID.randomUUID.toString.substring(0, 5)
}

case class Done()

class Bot extends Actor with ActorLogging {
  def receive: Receive = {
    case m =>
      log.debug(s"received $m")
  }
}
