package chat

final case class ChatMessage(from: ChatUser, to: ChatUser, message: Message)
