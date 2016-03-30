package mm4s.dockerbot

import spray.json.{DefaultJsonProtocol, RootJsonFormat}

object DeploymentModels {
  case class DeployRequest(name: String, image: String, ports: Seq[String])
  case class DeployResult(id: String, request: DeployRequest)
}

object DeploymentProtocols extends DefaultJsonProtocol {
  import DeploymentModels._

  implicit val deployFormat: RootJsonFormat[DeployRequest] = jsonFormat3(DeployRequest)
  implicit val deployResultFormat: RootJsonFormat[DeployResult] = jsonFormat2(DeployResult)
}
