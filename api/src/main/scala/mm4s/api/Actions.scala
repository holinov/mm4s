package mm4s.api

import spray.json._

import scala.reflect.ClassTag

sealed trait Action {
  def key: String
}

object Action {
  import Actions._

  def apply(str: String) = map().getOrElse(str, UnknownAction)
  def unapply(action: Action): Option[String] = Option(action.key)
}

object Actions {
  def list(): List[Action] = List(
    TypingAction, PostedAction, PostEditedAction, PostDeletedAction, ChannelViewedAction,
    NewUserAction, UserAddedAction, UserRemovedAction, PreferenceChangedAction, EphemeralMessageAction
  )
  def map(): Map[String, Action] = list.map(a => a.key -> a).toMap

  case object UnknownAction extends Action {val key = ""}
  case object TypingAction extends Action {val key = "typing"}
  case object PostedAction extends Action {val key = "posted"}
  case object PostEditedAction extends Action {val key = "post_edited"}
  case object PostDeletedAction extends Action {val key = "post_deleted"}
  case object ChannelViewedAction extends Action {val key = "channel_viewed"}
  case object NewUserAction extends Action {val key = "new_user"}
  case object UserAddedAction extends Action {val key = "user_added"}
  case object UserRemovedAction extends Action {val key = "user_removed"}
  case object PreferenceChangedAction extends Action {val key = "preference_changed"}
  case object EphemeralMessageAction extends Action {val key = "ephemeral_message"}
}

object ActionProtocol extends ActionSerializationSupport {
  implicit val ActionFormat: RootJsonFormat[Action] = caseObjectJsonFormat(Actions.list(): _*)
}

/**
 * JSON support for Action case objects
 * based on @agemooij from https://gitter.im/spray/spray/archives/2015/10/29
 */
trait ActionSerializationSupport extends DefaultJsonProtocol {

  def caseObjectJsonFormat[T <: Action : ClassTag](objects: T*)(implicit tag: ClassTag[T]) = new RootJsonFormat[T] {
    /** A mapping from object names to the objects */
    private val mapping = objects.map(obj ⇒ key(obj) -> obj).toMap

    override def read(json: JsValue): T = (json match {
      case JsString(value) ⇒ mapping.get(value)
      case _ ⇒ None
    }).getOrElse(deserializationError(s"Unknown json value found when converting to $tag: $json"))

    /** The toString value of a case object is its name */
    override def write(value: T): JsValue = JsString(key(value))

    private def key(input: T): String = Action.unapply(input).get
  }
}
