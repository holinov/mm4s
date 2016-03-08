package mm4s.examples

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import mm4s.api.{Teams, Streams}
import Streams._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 *
 */
object ListTeamsExample extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  val conn = connection("localhost")

  Teams.list("tyxn4dtr9ffoxk1tnnpxc7ygbo").via(conn).runForeach(println)


  Await.ready(system.whenTerminated, Duration.Inf)

  def rand() = UUID.randomUUID.toString.substring(0, 5)
}
