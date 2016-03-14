package mm4s.api

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import mm4s.api.ActionProtocol._
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}
import spray.json.{DefaultJsonProtocol, _}

/**
 * validate the Actions protocol
 */
class ActionsSpec extends WordSpec with Matchers with BeforeAndAfterAll with DefaultJsonProtocol {

  "protocol" should {
    Actions.list().map(ActionContainer).foreach { c =>
      s"serialize ${c.action.key}" in {
        c.toJson shouldBe s"""{"action":"${c.action.key}"}""".parseJson
      }
    }
  }

  case class ActionContainer(action: Action)
  implicit val actionContainerFormat: RootJsonFormat[ActionContainer] = jsonFormat1(ActionContainer)
}
