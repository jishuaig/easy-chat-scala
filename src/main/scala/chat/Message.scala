package chat

import com.alibaba.fastjson2.JSON

object Message:
  val MESSAGE_TYPE_TEXT = 1

@SerialVersionUID(-3714721708175778717L)
sealed abstract class Message extends Serializable:
  val messageType: Int
  def ofJsonBody(jsonBody: String): Message
  val toJsonBody: String

@SerialVersionUID(-7877668704353175789L)
case class TextMessage(val text: String) extends Message:
  val messageType = Message.MESSAGE_TYPE_TEXT
  override def ofJsonBody(jsonBody: String): Message = TextMessage.ofJsonBody(jsonBody)
  override val toJsonBody: String =
    JSON.toJSONString(this)

object TextMessage:
  def ofJsonBody(jsonBody: String): Message = 
    val text = JSON.parseObject(jsonBody).getString("text")
    new TextMessage(text)
