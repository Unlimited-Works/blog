package unlimited_works.play.util

import java.util.Date

import lorance.Log

trait LoggerMe {
  val logger: Log
  val context: LogContext
  val requestId: String //identity the request
  case class LogPair(key: String, value: String)

  def logWithTime(other: String) = logger.log( s"date=${new Date()} - $other")
  def logWithRequestId(other: String) = logWithTime( s"requestId=$requestId - $other")
  def logWithPath(other: String) = logWithRequestId(s"url=${context.uriInfo} - $other")
  def logWithGodSession(other: String) = logWithPath(s"GOD_SESSION=${context.godSession} - $other")
  def logWithUserIdOpt(other: String) = context.accountIdOpt.map(x => logWithGodSession(s"accountId=${x} - $other"))
  def logWithContext(other: String) = logWithUserIdOpt(getMethodName(5) + other)

  def getMethodName(index: Int) = {
    "methodName=" + Thread.currentThread().getStackTrace()(index) + " - "
  }
}

case class LogContext(uriInfo: String, godSession: String, accountIdOpt: Option[String])
