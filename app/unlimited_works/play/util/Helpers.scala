package unlimited_works.play.util

import java.security.MessageDigest

/**
  *
  */
trait MD5Helper {
  def stringTo32ByteMD5(raw: String): String = {
    val md5Bytes = MessageDigest.getInstance("MD5").
      digest((raw).getBytes)
    val hexValue = new StringBuffer
    for( c <- md5Bytes ) {
      val value = c.toInt & 0xff
      if (value < 16) hexValue.append("0")
      hexValue.append(Integer.toHexString(value))
    }
    hexValue.toString
  }
}

object Helpers extends MD5Helper
