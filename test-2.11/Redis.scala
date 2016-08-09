import com.redis.RedisClient

/**
  *
  */
object Redis extends App{
  val client = new RedisClient("127.0.0.1", 6379)
  val a = client.hset("GODSESSION", "accountId", "wh139130")

  val b = client.hget("GODSESSION", "accountId")

  println(s"a - $a; b - $b")

  val c = client.del("GODSESSION")

  val d = client.hgetall("GODSESSION")

  println(s"c - $c; d - $d")



}
