package mm4s.api

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.stream.scaladsl.Source
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

/**
 *
 */
object Teams {
  import Streams._
  import TeamModels._
  import TeamProtocols._

  def create(team: CreateTeam)(implicit system: ActorSystem): Source[HttpRequest, NotUsed] = {
    request("/teams/create") { r =>
      Marshal(team).to[MessageEntity].map(r.withMethod(HttpMethods.POST).withEntity)
    }
  }

  def find(name: String, token: String)(implicit system: ActorSystem): Source[HttpRequest, NotUsed] = {
    get("/teams/find_team_by_name").map(r =>
      r.withMethod(HttpMethods.POST).withHeaders(auth(token)).withEntity(s"""{"name":"$name"}""")
    )
  }

  def list(token: String)(implicit system: ActorSystem): Source[HttpRequest, NotUsed] = {
    get("/teams/all").map(withAuth(token))
  }
}

object TeamModels {
  case class Team(id: String, name: String)
  case class CreateTeam(display_name: String, name: String, email: String, `type`: String = "O")
  case class TeamCreated(id: String, display_name: String, name: String, email: String)
}

object TeamProtocols extends DefaultJsonProtocol with SprayJsonSupport {
  import TeamModels._

  implicit val CreateTeamFormat: RootJsonFormat[CreateTeam] = jsonFormat4(CreateTeam)
  implicit val TeamCreatedFormat: RootJsonFormat[TeamCreated] = jsonFormat4(TeamCreated)
  implicit val TeamFormat: RootJsonFormat[Team] = jsonFormat2(Team)
}
