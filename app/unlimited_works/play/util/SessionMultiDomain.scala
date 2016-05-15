package unlimited_works.play.util

import play.api.mvc.Request
import unlimited_works.play.controllers.util.Config

import scala.collection.mutable

/**
  * should be save a specify place,such as a memcache server
  */
object SessionMultiDomain {
  //out key is session id, inner key is every data's id
  private val sessions = mutable.Map[String, mutable.Map[String, String]]()

  def puts(id1: String, data: Map[String, String]): Unit = {
    sessions.synchronized{
      sessions.get(id1) match {
        case Some(theSession) => theSession ++= data
        case None => sessions.+=(id1 -> (mutable.Map[String, String]() ++= data))
      }
    }
  }

  def put(id1: String, data: (String, String)): Unit = {
    sessions.synchronized{
      sessions.get(id1) match {
        case Some(theSession) => theSession += data
        case None => sessions.+=(id1 -> (mutable.Map[String, String]() += data))
      }
    }
  }

  def removes(id1: String, id2: List[String]): Unit = {
    sessions.synchronized(sessions.get(id1).foreach{theS =>
      id2.foreach(theS -= _)
    })
  }

  def removeAll(id1: String): Unit = {
    sessions.synchronized(sessions.remove(id1))
  }

  def remove(id1: String, id2: String): Unit = {
    sessions.synchronized(sessions.get(id1).map(_.remove(id2)))
  }

  def gets(id1: String, id2: List[String]) = {
    sessions.synchronized{
      sessions.get(id1).map{ theS =>
        theS.filter{x => id2.contains(x._1)}.toMap
      }.getOrElse(Map[String, String]())
    }
  }

  def getAll(id1: String) = sessions.synchronized(sessions.get(id1))

  def get(id1: String, id2: String) = sessions.synchronized(sessions.get(id1).flatMap(_.get(id2)))

  def getAccountId[A](request: Request[A]) = {
    request.cookies.get(Config.CookieSession.GOD_SESSION).map(_.value).flatMap{
      get(_, Config.CookieSession.ACCOUNT_ID)
    }
  }
}
