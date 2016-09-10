package unlimited_works.play.util

//import com.redis.RedisClient
import play.api.mvc.Request
import unlimited_works.play.controllers.util.Config
import scredis._

import scala.concurrent.{ExecutionContext, Future}

object RedisService {
  val client = Redis(host = "127.0.0.1", port = 7777)
}

/**
  * should be save a specify place,such as a memcache server
  */
object SessionMultiDomain {
  //out key is session id, inner key is every data's id
//  private val sessions = mutable.Map[String, mutable.Map[String, String]]()
//  var redisClient = new RedisClient("127.0.0.1", 6379)
  val redisClient = RedisService.client

  def puts(id1: String, data: Map[String, String]): Future[Unit] = {
    redisClient.hmSet(id1, data)
//    redisClient.h()
//    client.
//    sessions.synchronized{
//      sessions.get(id1) match {
//        case Some(theSession) => theSession ++= data
//        case None => sessions.+=(id1 -> (mutable.Map[String, String]() ++= data))
//      }
//    }
  }

  def put(id1: String, data: (String, String)): Future[Boolean] = {
    redisClient.hSet(id1, data._1, data._2)

//    sessions.synchronized{
//      sessions.get(id1) match {
//        case Some(theSession) => theSession += data
//        case None => sessions.+=(id1 -> (mutable.Map[String, String]() += data))
//      }
//    }
  }

  def removes(id1: String, id2: Seq[String]): Future[Long] = {
    redisClient.hDel(id1, id2: _*)

//    sessions.synchronized(sessions.get(id1).foreach{theS =>
//      id2.foreach(theS -= _)
//    })
  }

  def removeAll(id1: String): Future[Long] = {
    redisClient.del(id1)

//    sessions.synchronized(sessions.remove(id1))
  }

  def remove(id1: String, id2: String): Future[Long] = {
    redisClient.hDel(id1, id2)

//    sessions.synchronized(sessions.get(id1).map(_.remove(id2)))
  }

  def gets(id1: String, id2: List[String])(implicit ec: ExecutionContext) = {
    redisClient.hGetAll(id1).map{x =>
      x.map(_.filter(y => id2.contains(y._1)))
    }
//    sessions.synchronized{
//      sessions.get(id1).map{ theS =>
//        theS.filter{x => id2.contains(x._1)}.toMap
//      }.getOrElse(Map[String, String]())
//    }
  }

  def getAll(id1: String) = {
    redisClient.hGetAll(id1)
//    sessions.synchronized(sessions.get(id1))
  }

  def get(id1: String, id2: String) = {
    redisClient.hGet(id1, id2)
//    sessions.synchronized(sessions.get(id1).flatMap(_.get(id2)))
  }

  def getAccountId[A](request: Request[A])(implicit ec: ExecutionContext) = {
    FutureEx.toFutureOpt(request.cookies.get(Config.CookieSession.GOD_SESSION).map(_.value).map{
      get(_, Config.CookieSession.ACCOUNT_ID)
    }).map(_.flatten)
  }
}
