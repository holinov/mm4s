package mm4s.bots

import java.util.UUID

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import com.rxthings.di._
import com.typesafe.scalalogging.LazyLogging
import mm4s.api.Streams._
import mm4s.api.UserModels.{LoggedInToChannel, LoginByUsername}
import mm4s.api.{Channels, Connection, Users}
import mm4s.bots.api.{Bot, ConfigKeys, Configuration, Register}
import net.ceedubs.ficus.Ficus._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 * Bootstrap a Bot
 */
object Boot extends App with LazyLogging {
  val config = Configuration.build()

  // todo;; should have configured system name
  val sysname = UUID.randomUUID.toString.take(7)
  implicit val system: ActorSystem = ActorSystem(s"dockerbot-$sysname", config)
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  import ConfigKeys._
  for {
    host <- config.getAs[String](key.host)
    port <- config.getAs[Int](key.port)
    user <- config.getAs[String](key.user)
    pass <- config.getAs[String](key.pass)
    team <- config.getAs[String](key.team)
    channel <- config.getAs[String](key.channel)
  } yield (host, port, user, pass, team, channel) match {
    case (_, _, _, _, _, _) =>
      println(s"host:$host port:$port user:$user pass:$pass team:$team chan:$channel")

      val bot: ActorRef = injectActor[Bot]
      val conn: Connection = connection(host, port)

      Users.login(LoginByUsername(user, pass, team))
      .via(conn)
      .via(Users.extractSession())
      .mapAsync(1) { s =>
        Channels.list(s.token)
        .via(conn).via(Channels.findany(channel))
        .map(c => c.map(c => LoggedInToChannel(s.token, c.id, s.details)).getOrElse(s))
        .runWith(Sink.head)
      }
      .runWith(Sink.actorRef(Mattermost(conn), Register(bot)))
    case _ =>
      logger.error("configuration failed h[{}],p[{}],u[{}],p[{}],t[{}],c[{}],", host, port.toString, user, pass, team, channel)
      system.terminate()
  }

  Await.ready(system.whenTerminated, Duration.Inf)
}
