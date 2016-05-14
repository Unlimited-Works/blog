package unlimited_works.play.util

/**
  * wapper for AES
  */
object AESSecurity {
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
