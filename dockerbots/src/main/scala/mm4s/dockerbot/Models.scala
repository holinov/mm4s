package mm4s.dockerbot

import spray.json.{JsObject, DefaultJsonProtocol, RootJsonFormat}

object DeploymentModels {
  /**
   *
   * @param name  human readable name for the service
   * @param image Docker image name
   * @param ports Docker style port string [host]:[container]
   * @param tags  directory service tags
   * @param env   Environment vars to set in the container
   * @param data  configuration to pass to bot as JSON
   */
  case class DeployRequest(name: String, image: String, ports: Seq[String], tags: Option[Seq[String]] = None, env: Option[Seq[String]] = None, data: Option[JsObject] = None)
  case class DeployResult(id: String, request: DeployRequest)
}

object DeploymentProtocols extends DefaultJsonProtocol {
  import DeploymentModels._

  implicit val deployFormat: RootJsonFormat[DeployRequest] = jsonFormat6(DeployRequest)
  implicit val deployResultFormat: RootJsonFormat[DeployResult] = jsonFormat2(DeployResult)
}
