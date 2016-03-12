package mm4s.api

sealed trait Action
case object UnknownAction extends Action
case object TypingAction extends Action
case object PostedAction extends Action
case object PostEditedAction extends Action
case object PostDeletedAction extends Action
case object ChannelViewedAction extends Action
case object NewUserAction extends Action
case object UserAddedAction extends Action
case object UserRemovedAction extends Action
case object PreferenceChangedAction extends Action
case object EphemeralMessageAction extends Action

object Action {
  def apply(str: String) = str match {
    case "typing" => TypingAction
    case "posted" => PostedAction
    case "post_edited" => PostEditedAction
    case "post_deleted" => PostDeletedAction
    case "channel_viewed" => ChannelViewedAction
    case "new_user" => NewUserAction
    case "user_added" => UserAddedAction
    case "user_removed" => UserRemovedAction
    case "preference_changed" => PreferenceChangedAction
    case "ephemeral_message" => EphemeralMessageAction
    case _ => UnknownAction
  }
}
