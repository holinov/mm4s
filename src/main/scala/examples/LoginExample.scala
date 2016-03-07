package examples

import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.headers.`Set-Cookie`
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
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

  Users.login(logindata)
  .via(conn)
  .via(Users.extractToken)
  .runForeach(println)

  Await.ready(system.whenTerminated, Duration.Inf)

  def rand() = UUID.randomUUID.toString.substring(0, 5)
}
