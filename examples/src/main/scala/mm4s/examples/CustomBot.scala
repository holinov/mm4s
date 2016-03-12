package mm4s.examples

import akka.actor._
import mm4s.bots.api.{BotID, Message, Ready}
import net.codingwell.scalaguice.ScalaModule


class CustomBot extends Actor with mm4s.bots.api.Bot with ActorLogging {
  var api: Option[ActorRef] = None
  var id: Option[BotID] = None

  def receive: Receive = {
    case Ready(mm, botid) =>
      api = Option(mm)
      id = Option(botid)
      log.debug("Bot [{}] ready", id.get /* hack */ .username)

      api.foreach(_ ! Message("Here's Botty!"))

    case Message(t) =>
      log.debug("{} received {}", self.path.name, t)
  }
}

class CustomBotModule extends ScalaModule {
  def configure(): Unit = {
    bind[mm4s.bots.api.Bot].to[CustomBot]
  }
}
