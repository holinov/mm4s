package mm4s

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling._
import akka.http.scaladsl.model.headers.`Set-Cookie`
import akka.http.scaladsl.model.{HttpMethods, HttpResponse, MessageEntity}
import akka.stream.scaladsl.Flow
import spray.json._


/**
 * User related API components
 *
 * @see [[https://github.com/mattermost/platform/blob/master/api/user.go]]
 */
object Users {
  import Streams._
  import UserModels._
  import UserProtocols._

  def create(createUser: CreateUser)(implicit system: ActorSystem) = {
    request("/users/create") { r =>
      Marshal(createUser).to[MessageEntity].map(r.withMethod(HttpMethods.POST).withEntity)
    }
  }

  def login(byEmail: LoginByEmail)(implicit system: ActorSystem) = {
    request("/users/login") { r =>
      Marshal(byEmail).to[MessageEntity].map(r.withMethod(HttpMethods.POST).withEntity)
    }
  }

  val extractToken = Flow[HttpResponse].map(_.headers.collect { case `Set-Cookie`(x) ⇒ x })
}


object UserModels {
  case class CreateUser(username: String, password: String, email: String, team_id: String)
  case class UserCreated(id: String, username: String, email: String, team_id: String)
  case class LoginByEmail(email: String, password: String, name: String /*team name*/)
}

object UserProtocols extends DefaultJsonProtocol with SprayJsonSupport {
  import UserModels._

  implicit val CreateUserFormat: RootJsonFormat[CreateUser] = jsonFormat4(CreateUser)
  implicit val UserCreatedFormat: RootJsonFormat[UserCreated] = jsonFormat4(UserCreated)
  implicit val LoginByEmailFormat: RootJsonFormat[LoginByEmail] = jsonFormat3(LoginByEmail)
}
