package mm4s.bots.api

import akka.actor.ActorRef

/**
 * sent to bots to alert them that the API is ready to work
 *
 * @param api The MM API Actor
 */
case class Ready(api: ActorRef, id: BotID)
