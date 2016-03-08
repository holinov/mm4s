package mm4s.examples

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import mm4s.api.{TeamProtocols, TeamModels, Teams, Streams}
import Streams._
import mm4s._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 *
 */
object CreateTeamExample extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  import TeamModels._
  import TeamProtocols._

  val conn = connection("localhost")
  val email = s"${rand()}@bar.com"
  val name = s"myteam${rand()}"
  val team = CreateTeam(s"$name Display Name", name, email)

  Teams.create(team)
  .via(conn)
  .via(response[TeamCreated])
  .runForeach(println)

  Await.ready(system.whenTerminated, Duration.Inf)

  def rand() = UUID.randomUUID.toString.substring(0, 5)
}
