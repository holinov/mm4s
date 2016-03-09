package mm4s.bots.api

import akka.actor.ActorRef
import akka.http.scaladsl.model.{HttpResponse, HttpRequest}
import akka.stream.scaladsl.Flow

/**
 * Notify the Bot implementation of a successful connection
 */
case class Connected(bot: ActorRef, conn: Flow[HttpRequest, HttpResponse, _])
