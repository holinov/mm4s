package examples

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import mm4s.Streams._
import mm4s.UserModels.{CreateUser, UserCreated}
import mm4s.{UserProtocols, Users}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 *
 */
object CreateUserExample extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  val user = s"user${rand()}"

  import UserProtocols._

  Users
  .create(CreateUser(user, "password", s"$user@bar.com", "63nkdfwn9fn9tfmqj7tyrpyk7w"))
  .via(connection("localhost"))
  .via(response[UserCreated])
  .runForeach(println)

  Await.ready(system.whenTerminated, Duration.Inf)

  def rand() = UUID.randomUUID.toString.substring(0, 5)
}
