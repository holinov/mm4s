package mm4s

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.HttpHeader.ParsingResult.Ok
import akka.http.scaladsl.model._
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import scala.concurrent.Future

/**
 *
 */
object Teams {
  import Streams._
  import TeamModels._
  import TeamProtocols._

  def create(team: CreateTeam)(implicit system: ActorSystem) = {
    request("/teams/create") { r =>
      Marshal(team).to[MessageEntity].map(r.withMethod(HttpMethods.POST).withEntity)
    }
  }

  def list(token: String)(implicit system: ActorSystem) = {
    // todo;; temporary hanlding of headers
    val h: Ok = HttpHeader.parse("Cookie", token).asInstanceOf[Ok]
    get("/teams/all").map(r => r.withHeaders(h.header))
  }
}

object TeamModels {
  case class CreateTeam(display_name: String, name: String, email: String, `type`: String = "O")
  case class TeamCreated(id: String, display_name: String, name: String, email: String)
}

object TeamProtocols extends DefaultJsonProtocol with SprayJsonSupport {
  import TeamModels._

  implicit val CreateTeamFormat: RootJsonFormat[CreateTeam] = jsonFormat4(CreateTeam)
  implicit val TeamCreatedFormat: RootJsonFormat[TeamCreated] = jsonFormat4(TeamCreated)
}
