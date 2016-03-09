package mm4s.bots.api

import akka.actor.Actor

/**
 * Interface to a mm4s Bot
 */
trait Bot extends Actor

case class BotID(username: String)
