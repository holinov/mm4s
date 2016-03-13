package mm4s.dockerbot

import com.typesafe.config.{Config, ConfigFactory}
import mm4s.bots.api.ConfigKeys
import mm4s.bots.api.ConfigKeys.{env, key}
import net.ceedubs.ficus.Ficus._

import scala.collection.JavaConversions._

/**
 *
 */
object Configuration {

  def build(config: Config = ConfigFactory.load())(implicit envs: Map[String, String] = sys.env): Config = {
    import ConfigKeys._
    import ConfigKeys.defaults._
    val host = resolve(env.host, key.host, Option(mmHost), config, envs)
    val port = resolve(env.port, key.port, Option(mmPort), config, envs)
    val user = resolve(env.user, key.user, None, config, envs)
    val pass = resolve(env.pass, key.pass, None, config, envs)
    val team = resolve(env.team, key.team, None, config, envs)
    val channel = resolve(env.channel, key.channel, None, config, envs)

    ConfigFactory.parseMap(Map(
      key.host -> host,
      key.port -> port.map(_.toInt).map(Int.box),
      key.user -> user,
      key.pass -> pass,
      key.team -> team,
      key.channel -> channel
    ).filter(_._2.isDefined).map(e => e._1 -> e._2.get)).withFallback(config)
  }

  def resolve(env: String, key: String, default: Option[String], cfg: Config, envs: Map[String, String]): Option[String] =
    envs.get(env).orElse(cfg.getAs[String](key)).orElse(default)
}
