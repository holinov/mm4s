package mm4s.dockerbot

import spray.json.{DefaultJsonProtocol, RootJsonFormat}

object DeployModels {
  case class Deploy(name: String, image: String, ports: Seq[String])
  case class DeployResult(id: String, source: Deploy)
}

object DeployProtocols extends DefaultJsonProtocol {
  import DeployModels._

  implicit val deployFormat: RootJsonFormat[Deploy] = jsonFormat3(Deploy)
  implicit val deployResultFormat: RootJsonFormat[DeployResult] = jsonFormat2(DeployResult)
}
