package mqtt.codec

import com.caucho.hessian.io.{HessianInput, HessianOutput}

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, IOException}


object HessianSerializer {

  def serialize[T](obj: T): Array[Byte] =
    val byteArrayOutputStream = new ByteArrayOutputStream()
    val hessianOutput = new HessianOutput(byteArrayOutputStream)
    try
      hessianOutput.writeObject(obj)
      hessianOutput.flush()
      byteArrayOutputStream.toByteArray
    catch
      case e: Exception => throw e
    finally
      hessianOutput.close()
      byteArrayOutputStream.close()


  def deserialize[T](bytes: Array[Byte], clazz: Class[T]): T =
    val byteArrayInputStream = new ByteArrayInputStream(bytes)
    val hessianInput = new HessianInput(byteArrayInputStream)
    try
      hessianInput.readObject(clazz).asInstanceOf[T]
    catch
      case e: Exception => throw e
    finally
      byteArrayInputStream.close()
      hessianInput.close()

}
