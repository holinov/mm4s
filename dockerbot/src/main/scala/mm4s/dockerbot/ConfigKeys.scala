package mm4s.dockerbot


object ConfigKeys {
  object env {
    val host = "MM_HOST"
    val user = "BOT_USER"
    val pass = "BOT_PASS"
    val team = "BOT_TEAM"
    val channel = "BOT_CHANNEL"
  }

  object key {
    val host = "mm.host"
    val user = "mm.user"
    val pass = "mm.pass"
    val team = "mm.team"
    val channel = "mm.channel"
  }

  val mmHost = "mattermost"
  val mmUser = "bot"
  val mmPass = "password"
  val mmTeam = "bots"
  val mmChannel = "Town Center"
}
