package mm4s.bots

import java.util.UUID

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import com.rxthings.di._
import com.typesafe.scalalogging.LazyLogging
import mm4s.api.Streams._
import mm4s.api.UserModels.{LoggedIn, LoggedInToChannel, LoginByUsername}
import mm4s.api.{Channels, Connection, Users}
import mm4s.bots.api.{Bot, ConfigKeys, Configuration, Register}
import net.ceedubs.ficus.Ficus._

import scala.concurrent.{Future, Await}
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
  } yield (host, port, user, pass, team) match {
    case (_, _, _, _, _) =>
      logger.info("host:{} port:{} user:{} pass:{} team:{}", host, port.toString, user, pass, team)

      val bot: ActorRef = injectActor[Bot]
      val conn: Connection = connection(host, port)
      val channel = config.getAs[String](key.channel)

      Users.login(LoginByUsername(user, pass, team))
      .via(conn)
      .via(Users.extractSession())
      .mapAsync(1) { s =>
        channel.map { ch =>
          Channels.list(s.token)
          .via(conn).via(Channels.findany(ch))
          .map(c => c.map(c => LoggedInToChannel(s.token, c.id, s.details)).getOrElse(s))
          .runWith(Sink.head)
        }.getOrElse(Future.successful(LoggedIn(s.token, s.details)))
      }
      .runWith(Sink.actorRef(Gateway(conn), Register(bot)))
    case _ =>
      logger.error("configuration failed h[{}],p[{}],u[{}],p[{}],t[{}],c[{}],", host, port.toString, user, pass, team)
      system.terminate()
  }

  Await.ready(system.whenTerminated, Duration.Inf)
}
