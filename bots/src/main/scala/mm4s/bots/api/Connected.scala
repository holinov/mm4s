package mm4s.bots.api

import akka.actor.ActorRef

/**
 * Notify the Bot implementation of a successful connection
 */
case class Connected(mm: ActorRef)
