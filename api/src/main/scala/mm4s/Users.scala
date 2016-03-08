package mm4s

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling._
import akka.http.scaladsl.model.headers.`Set-Cookie`
import akka.http.scaladsl.model.{HttpMethods, HttpResponse, MessageEntity}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
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

  def login(byUsername: LoginByUsername)(implicit system: ActorSystem) = {
    request("/users/login") { r =>
      Marshal(byUsername).to[MessageEntity].map(r.withMethod(HttpMethods.POST).withEntity)
    }
  }

  def extractSession()(implicit system: ActorSystem, mat: ActorMaterializer) = {
    Flow[HttpResponse].mapAsync(1) { r =>
      val cookie = r.headers.collect { case `Set-Cookie`(x) ⇒ x }.head.value
      Unmarshal(r).to[LoginDetails].map(d => LoggedIn(cookie, d))
    }
  }
}


object UserModels {
  case class CreateUser(username: String, password: String, email: String, team_id: String)
  case class UserCreated(id: String, username: String, email: String, team_id: String)
  case class LoginByUsername(username: String, password: String, name: String /*team name*/)
  case class LoginDetails(id: String, team_id: String, username: String, email: String)
  case class LoggedIn(token: String, details: LoginDetails)
}

object UserProtocols extends DefaultJsonProtocol with SprayJsonSupport {
  import UserModels._

  implicit val CreateUserFormat: RootJsonFormat[CreateUser] = jsonFormat4(CreateUser)
  implicit val UserCreatedFormat: RootJsonFormat[UserCreated] = jsonFormat4(UserCreated)
  implicit val LoginByUsernameFormat: RootJsonFormat[LoginByUsername] = jsonFormat3(LoginByUsername)
  implicit val LoginDetailsFormat: RootJsonFormat[LoginDetails] = jsonFormat4(LoginDetails)
  implicit val LoggedInFormat: RootJsonFormat[LoggedIn] = jsonFormat2(LoggedIn)
}
