package mm4s.dockerbot

import com.typesafe.config.{Config, ConfigFactory}
import net.ceedubs.ficus.Ficus._

import scala.collection.JavaConversions._


object Configuration {
  def build(config: Config = ConfigFactory.load())(implicit envs: Map[String, String] = sys.env) = {
    val host = resolve(env.host, key.host, Option(defaults.host), config, envs)
    val port = resolve(env.port, key.port, Option(defaults.port), config, envs)

    ConfigFactory.parseMap(Map(
      key.host -> host,
      key.port -> port.map(_.toInt).map(Int.box)
    ).filter(_._2.isDefined).map(e => e._1 -> e._2.get)).withFallback(config)
  }

  def resolve(env: String, key: String, default: Option[String], cfg: Config, envs: Map[String, String]): Option[String] =
    envs.get(env).orElse(cfg.getAs[String](key)).orElse(default)

  object env {
    val host = "DOCKERBOT_HOST"
    val port = "DOCKERBOT_PORT"
  }

  object key {
    val host = "dockerbot.host"
    val port = "dockerbot.port"
  }

  object defaults {
    val host = "0.0.0.0"
    val port = "8080"
  }
}
