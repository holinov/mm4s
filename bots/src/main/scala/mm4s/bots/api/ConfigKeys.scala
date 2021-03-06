package mm4s.bots.api

object ConfigKeys {
  object env {
    val host = "MM_HOST"
    val port = "MM_PORT"
    val user = "BOT_USER"
    val pass = "BOT_PASS"
    val team = "BOT_TEAM"
    val channel = "BOT_CHANNEL"
  }

  object key {
    val host = "mm.host"
    val port = "mm.port"
    val user = "mm.bot.user"
    val pass = "mm.bot.pass"
    val team = "mm.bot.team"
    val channel = "mm.bot.channel"
  }

  object defaults {
    val mmHost = "mattermost"
    val mmPort = "80"
  }
}
