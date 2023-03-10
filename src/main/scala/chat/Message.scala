package chat

import com.alibaba.fastjson2.JSON

object Message:
  val MESSAGE_TYPE_TEXT = 1
  val MESSAGE_TYPE_IMAGE = 2

sealed abstract class Message extends Serializable:
  val messageType: Int

  def ofJsonBody(jsonBody: String): Message

  val toJsonBody: String = JSON.toJSONString(this)

case class TextMessage(val text: String) extends Message:
  val messageType = Message.MESSAGE_TYPE_TEXT

  override def ofJsonBody(jsonBody: String): Message =
    val text = JSON.parseObject(jsonBody).getString("text")
    TextMessage(text)


case class ImageMessage(val height: Int, val width: Int, origin: String, tiny: String) extends Message:
  val messageType = Message.MESSAGE_TYPE_IMAGE

  override def ofJsonBody(jsonBody: String): Message =
    val jsonObj = JSON.parseObject(jsonBody)
    ImageMessage(jsonObj.getIntValue("height"), jsonObj.getIntValue("width"),
      jsonObj.getString("origin"), jsonObj.getString("tiny"))
