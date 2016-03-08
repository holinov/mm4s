package examples

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import mm4s.Streams._
import mm4s.Teams

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
