package examples

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink}
import mm4s.Streams._
import mm4s.UserModels.{LoginByUsername, CreateUser, UserCreated}
import mm4s.{UserProtocols, Users}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 *
 */
object CreateUserExample extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  import UserProtocols._

  val user = s"user${rand()}"
  val pass = "password"
  val conn = connection("localhost")
  val email = s"$user@bar.com"
  val team_id = "63nkdfwn9fn9tfmqj7tyrpyk7w"
  val userdata = CreateUser(user, pass, email, team_id)

  // transition from creating user to logging user in
  // todo;; extract the Token from the response headers
  val toLogin = Flow[UserCreated]
                .map(c => LoginByUsername(c.username, pass, "xxxx" /* todo;; requires the team-name not the id */))
                .mapAsync(1)(l => Users.login(l).via(conn).runWith(Sink.head))

  Users.create(userdata)
  .via(conn)
  .via(response[UserCreated])
  .via(toLogin)
  .runForeach(println)

  Await.ready(system.whenTerminated, Duration.Inf)

  def rand() = UUID.randomUUID.toString.substring(0, 5)
}
