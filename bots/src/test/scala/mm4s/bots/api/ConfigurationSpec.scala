package mm4s.bots.api

import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.Ficus._
import org.scalatest.{Matchers, WordSpec}

import scala.collection.JavaConversions._


/**
 *
 */
class ConfigurationSpec extends WordSpec with Matchers {
  import ConfigKeys.key._

  "building" should {
    val expectedhost = "host"
    val expectedhost2 = "host2"

    implicit val envs = Map[String, String]("MM_HOST" -> expectedhost)

    "give highest priority to env var" in {
      val config = ConfigFactory.parseMap(Map(host -> expectedhost2))
      Configuration.build(config).as[String](host) shouldBe expectedhost
    }

    "prefer configuration over default" in {
      val config = ConfigFactory.parseMap(Map(host -> expectedhost2))
      Configuration.build(config)(Map()).as[String](host) shouldBe expectedhost2
    }

    "use defaults" in {
      val config = ConfigFactory.parseMap(Map(host -> expectedhost2))
      Configuration.build(config)(Map()).as[String](port) shouldBe ConfigKeys.defaults.mmPort
    }
  }
}
