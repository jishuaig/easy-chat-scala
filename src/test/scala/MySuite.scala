import chat.TextMessage
import mqtt.codec.HessianSerializer

// For more information on writing tests, see
// https://scalameta.org/munit/docs/getting-started.html
class MySuite extends munit.FunSuite {
  test("example test that succeeds") {
    val obtained = 42
    val expected = 42
    assertEquals(obtained, expected)
  }

  test("hessian serialize") {
    val msg = TextMessage("123")
    val bytes = HessianSerializer.serialize(msg)
    val msg2 = HessianSerializer.deserialize(bytes, classOf[TextMessage])
    println(msg2)
    assertEquals(msg, msg2)
  }
}
