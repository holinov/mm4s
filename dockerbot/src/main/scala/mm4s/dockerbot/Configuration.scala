package mm4s.dockerbot

import com.typesafe.config.{Config, ConfigFactory}
import net.ceedubs.ficus.Ficus._

import scala.collection.JavaConversions._

/**
 *
 */
object Configuration {

  def build(config: Config = ConfigFactory.load())(implicit envs: Map[String, String] = sys.env): Config = {
    import ConfigKeys._
    import ConfigKeys.defaults._
    val host = resolve(env.host, key.host, mmHost, config, envs)
    val port = resolve(env.port, key.port, mmPort, config, envs)
    val user = resolve(env.user, key.user, botUser, config, envs)
    val pass = resolve(env.pass, key.pass, botPass, config, envs)
    val team = resolve(env.team, key.team, botTeam, config, envs)
    val channel = resolve(env.channel, key.channel, botChannel, config, envs)

    ConfigFactory.parseMap(Map(
      key.host -> host,
      key.port -> Int.box(port.toInt),
      key.user -> user,
      key.pass -> pass,
      key.team -> team,
      key.channel -> channel
    )).withFallback(config)
  }

  def resolve(env: String, key: String, default: String, cfg: Config, envs: Map[String, String]) =
    envs.get(env).orElse(cfg.getAs[String](key)).getOrElse(default)
}
