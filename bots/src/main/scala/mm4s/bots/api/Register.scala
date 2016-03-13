package mm4s.bots.api

import akka.actor.ActorRef
import akka.http.scaladsl.model.{HttpResponse, HttpRequest}
import akka.stream.scaladsl.Flow

/**
 * Register the Bot with the API
 */
case class Register(bot: ActorRef)
