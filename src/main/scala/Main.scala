import chat._
@main def hello: Unit =
  val from = ChatUser(1, "qq")
  val to = ChatUser(2, "yy")
  val textMessage = TextMessage("123")
  val chatMessage = ChatMessage(from, to, textMessage)
  println(chatMessage)
  println(textMessage.toJsonBody)
   println(textMessage.ofJsonBody())

