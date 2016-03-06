package mm4s

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling._
import akka.http.scaladsl.model.{HttpResponse, MessageEntity}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.Flow
import spray.json._


/**
 * User related API components
 * @see [[https://github.com/mattermost/platform/blob/master/api/user.go]]
 */
object Users {
  import Streams._
  import UserModels._
  import UserProtocols._

  def create(createUser: CreateUser)(implicit system: ActorSystem) = {
    request("/users/create") {
      Marshal(createUser).to[MessageEntity]
    }
  }

  def login(byEmail: LoginByEmail)(implicit system: ActorSystem) = {
    request("/users/login") {
      Marshal(byEmail).to[MessageEntity]
    }
  }
}


object UserModels {
  case class CreateUser(username: String, password: String, email: String, team_id: String)
  case class UserCreated(id: String, username: String, email: String, team_id: String)
  case class LoginByEmail(email: String, password: String, team: String)
}

object UserProtocols extends DefaultJsonProtocol with SprayJsonSupport {
  import UserModels._

  implicit val CreateUserFormat: RootJsonFormat[CreateUser] = jsonFormat4(CreateUser)
  implicit val UserCreatedFormat: RootJsonFormat[UserCreated] = jsonFormat4(UserCreated)
  implicit val LoginByEmailFormat: RootJsonFormat[LoginByEmail] = jsonFormat3(LoginByEmail)
}
