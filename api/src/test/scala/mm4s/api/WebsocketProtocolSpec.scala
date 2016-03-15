package mm4s.api

import mm4s.api.Actions.PostedAction
import mm4s.api.WebSocketModels.WebSocketMessage
import mm4s.api.WebSocketProtocol._
import org.scalatest.{Matchers, WordSpec}
import spray.json._

class WebsocketProtocolSpec extends WordSpec with Matchers {
  val json =
    """
      |{
      |    "team_id": "tid",
      |    "channel_id": "cid",
      |    "user_id": "uid",
      |    "action": "posted",
      |    "props": {
      |        "channel_type": "O",
      |        "otherFile": "true",
      |        "post": "{\"id\":\"pid\",\"create_at\":1,\"update_at\":2,\"delete_at\":0,\"user_id\":\"puid\",\"channel_id\":\"pcid\",\"root_id\":\"\",\"parent_id\":\"\",\"original_id\":\"\",\"message\":\"msg\",\"type\":\"\",\"props\":{},\"hashtags\":\"#bar #baz #bash\",\"filenames\":[\"/a/b/c/d.pdf\"],\"pending_post_id\":\"pppid\"}"
      |    }
      |}
    """.stripMargin

  "WebSocket Message" should {
    "deserialize" in {
      val message = json.parseJson.convertTo[WebSocketMessage]

      message.team_id shouldBe "tid"
      message.channel_id shouldBe "cid"
      message.user_id shouldBe "uid"
      message.action shouldBe PostedAction
      message.props.channel_type shouldBe Some("O")
      message.props.otherFile shouldBe Some("true")

      message.props.posted shouldBe defined
      val posted = message.props.posted.get
      posted.id shouldBe "pid"
      posted.create_at shouldBe 1
      posted.user_id shouldBe "puid"
      posted.channel_id shouldBe "pcid"
      posted.message shouldBe "msg"
      posted.hashtags shouldBe "#bar #baz #bash"
      posted.filenames shouldBe Seq("/a/b/c/d.pdf")
    }
  }
}
