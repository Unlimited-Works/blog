package unlimited_works.play.util

import org.apache.commons.codec.binary.Base64
/**
  * wapper for AES
  */
trait AESSecurity {
  def encrypt(content: String, key: String) = {
    new String(AES.encrypt(content.getBytes("utf-8"), key.getBytes("utf-8")), "utf-8")
  }

  def decrypt(content: String, key: String) = {
    try{
      AES.decrypt(content.getBytes("utf-8"), key.getBytes("utf-8"))
      true
    } catch {
      case e: ArrayIndexOutOfBoundsException => false
    }
  }
}

object AESSecurity extends AESSecurity

//object SessionSecurity extends AESSecurity {
//  override def encrypt(content: String, key: String) = {
//    val encodedBytes = Base64.encodeBase64("Test".getBytes())
//    new String(AES.encrypt(content.getBytes("utf-8"), key.getBytes("utf-8")), "utf-8")
//  }
//
//  override def decrypt(content: String, key: String) = {
//    val decodedBytes = Base64.decodeBase64(content)
//
//    try{
//      AES.decrypt(content.getBytes("utf-8"), key.getBytes("utf-8"))
//      true
//    } catch {
//      case e: ArrayIndexOutOfBoundsException => false
//    }
//  }
//}