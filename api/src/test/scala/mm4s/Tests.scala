package mm4s

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.stream.scaladsl.Flow
import org.scalatest.Tag

/**
 *
 */
object Tests {
  object DockerTest extends Tag("mm4s.DockerTest")

  def failOnNotOK = Flow[HttpResponse].map {
    case r @ HttpResponse(StatusCodes.OK, _, _, _) => r
    case r => throw new RuntimeException(s"Request failed on [${r}]")
  }
}
