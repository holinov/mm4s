package mm4s.api

sealed trait Action
case object Unknown extends Action
case object Typing extends Action
case object Posted extends Action
case object PostEdited extends Action
case object PostDeleted extends Action
case object ChannelViewed extends Action
case object NewUser extends Action
case object UserAdded extends Action
case object UserRemoved extends Action
case object PreferenceChanged extends Action
case object EphemeralMessage extends Action

object Action {
  def apply(str: String) = str match {
    case "typing" => Typing
    case "posted" => Posted
    case "post_edited" => PostEdited
    case "post_deleted" => PostDeleted
    case "channel_viewed" => ChannelViewed
    case "new_user" => NewUser
    case "user_added" => UserAdded
    case "user_removed" => UserRemoved
    case "preference_changed" => PreferenceChanged
    case "ephemeral_message" => EphemeralMessage
    case _ => Unknown
  }
}
