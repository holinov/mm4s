package mm4s.examples

import akka.actor._
import mm4s.api.Posted
import mm4s.bots.api.{BotID, Ready}
import net.codingwell.scalaguice.ScalaModule


class CustomBot extends Actor with mm4s.bots.api.Bot with ActorLogging {

  def receive: Receive = {
    case Ready(api, id) =>
      context.become(ready(api, id))
  }

  def ready(api: ActorRef, id: BotID): Receive = {
    log.debug("Bot [{}] ready", id.username)

    {
      case Posted(t) =>
        log.debug("{} received {}", self.path.name, t)
    }
  }
}

class CustomBotModule extends ScalaModule {
  def configure(): Unit = {
    bind[mm4s.bots.api.Bot].to[CustomBot]
  }
}
