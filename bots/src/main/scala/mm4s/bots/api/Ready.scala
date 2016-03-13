package mm4s.bots.api

import akka.actor.ActorRef

/**
 * Notify the Bot implementation that the API is ready
 */
case class Ready(api: ActorRef, id: BotID)
