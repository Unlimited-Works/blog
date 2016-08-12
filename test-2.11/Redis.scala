import com.redis.RedisClient
import net.liftweb.json._
/**
  *
  */
object Redis extends App{
  val client = new RedisClient("127.0.0.1", 6379)
  implicit val f = DefaultFormats
//  val a = client.hset("GODSESSION", "accountId", "wh139130")
//
//  val b = client.hget("GODSESSION", "accountId")
//
//  println(s"a - $a; b - $b")
//
//  val c = client.del("GODSESSION")
//
//  val d = client.hgetall("GODSESSION")
//
//  println(s"c - $c; d - $d")
//  val x = client.hset("7ce36fe6c98b8e2ca2d5a80c8eeec569", "accountId", "1231231322")
  val jstr = compactRender(Extraction.decompose(Content("wh139130", "elpsy")))
  val b = client.hset("7ce36fe6c98b8e2ca2d5a80c8eeec569", "remember-me", jstr)
  val y = client.hget("7ce36fe6c98b8e2ca2d5a80c8eeec569", "remember-me")

  y
}
case class Content(account: String, password:String)