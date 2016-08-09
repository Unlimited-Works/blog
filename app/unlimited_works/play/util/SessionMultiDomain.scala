package unlimited_works.play.util

import com.redis.RedisClient
import play.api.mvc.Request
import unlimited_works.play.controllers.util.Config

/**
  * should be save a specify place,such as a memcache server
  */
object SessionMultiDomain {
  //out key is session id, inner key is every data's id
//  private val sessions = mutable.Map[String, mutable.Map[String, String]]()
  var redisClient = new RedisClient("127.0.0.1", 6379)

  def puts(id1: String, data: Map[String, String]): Unit = {
    redisClient.hmset(id1, data)
//    redisClient.h()
//    client.
//    sessions.synchronized{
//      sessions.get(id1) match {
//        case Some(theSession) => theSession ++= data
//        case None => sessions.+=(id1 -> (mutable.Map[String, String]() ++= data))
//      }
//    }
  }

  def put(id1: String, data: (String, String)): Unit = {
    redisClient.hset(id1, data._1, data._2)

//    sessions.synchronized{
//      sessions.get(id1) match {
//        case Some(theSession) => theSession += data
//        case None => sessions.+=(id1 -> (mutable.Map[String, String]() += data))
//      }
//    }
  }

  def removes(id1: String, id2: List[String]): Unit = {
    redisClient.hdel(id1, id2)

//    sessions.synchronized(sessions.get(id1).foreach{theS =>
//      id2.foreach(theS -= _)
//    })
  }

  def removeAll(id1: String): Unit = {
    redisClient.del(id1)

//    sessions.synchronized(sessions.remove(id1))
  }

  def remove(id1: String, id2: String): Unit = {
    redisClient.hdel(id1, id2)

//    sessions.synchronized(sessions.get(id1).map(_.remove(id2)))
  }

  def gets(id1: String, id2: List[String]) = {
    redisClient.hgetall(id1).map{x =>
      x.filter(y => id2.contains(y._1))
    }
//    sessions.synchronized{
//      sessions.get(id1).map{ theS =>
//        theS.filter{x => id2.contains(x._1)}.toMap
//      }.getOrElse(Map[String, String]())
//    }
  }

  def getAll(id1: String) = {
    redisClient.hgetall(id1)
//    sessions.synchronized(sessions.get(id1))
  }

  def get(id1: String, id2: String) = {
    redisClient.hget(id1, id2)
//    sessions.synchronized(sessions.get(id1).flatMap(_.get(id2)))
  }

  def getAccountId[A](request: Request[A]) = {
    request.cookies.get(Config.CookieSession.GOD_SESSION).map(_.value).flatMap{
      get(_, Config.CookieSession.ACCOUNT_ID)
    }
  }
}
