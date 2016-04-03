package unlimited_works.play.socket.dao.module

import java.util.concurrent.TimeoutException

import lorance.rxscoket.presentation.json.IdentityTask
import rx.lang.scala.Observable
import unlimited_works.play.socket.DaoCommunicate
import unlimited_works.play.socket.dao.Method

import lorance.rxscoket.presentation.getTaskId

import scala.concurrent.ExecutionContext.Implicits.global
import net.liftweb.json._

import scala.concurrent.Promise

/**
  * {
  * "taskId": "thread-1:timestamp",//if not have represent needn't call back
    *"dataBase": "helloworld",
    *"collection": "accounts",
  * ...
  *}
  */
object LoginModule {
  val dBName = "helloworld"
  val collName = "account"
  lorance.rxscoket.presentation.TIMEOUT = 10
  /**
    * find -
    * method: aggregate
    *"params": [
    *   { $match: {"$or": [
      *     { "username": "administrator"},
      *     { "phone":"110"},
      *     { "email":"123@xxx.com"},
      *     { "pen_name":"lorancechen"}
    *     ]},
    *     "password": "12345_md5"}
    *   },
    *   { $limit: 1 },
    *   { $project: { _id: 1 } }
    * ]
    */
  def verifyAndGetId(account: String, password: String) = {
    val proto = AccountVerifyByAggregate(getTaskId, dBName, collName, Method.AGGREGATE,
      List(AggMatch(Match(List(UserName(account), Phone(account), Email(account), PenName(account)), password)), Limit(1), Project(IdProject(1))))
    implicit val formats = DefaultFormats

    DaoCommunicate.getDefaultInstance.flatMap{o =>
      val x = o.sendWithResult[AccountVerifyByAggregateResult, AccountVerifyByAggregate](proto, Some(x => x.result.nonEmpty))
      toFuture(x)
    }
//    DaoCommunicate.getDefaultInstance.sendWithResult[AccountVerifyByAggregateResult, AccountVerifyByAggregate](proto)
  }

  private def toFuture(observable: Observable[AccountVerifyByAggregateResult]) = {
    val p = Promise[AccountVerifyByAggregateResult]
    observable.subscribe(
      s => {println(s"verifyAndGetId get Promise value"); p.trySuccess(s)},
      e => {println(s"verifyAndGetId get Promise Error"); p.tryFailure(e)},
      () => {println(s"verifyAndGetId get Completed");  p.tryFailure(new TimeoutException())}
    )

    p.future
  }
}

/**
  * find a account by aggregate limit return _id
  */
case class AccountVerifyByAggregate(taskId: String, dataBase: String, collection: String, method: String, params: List[AggregatePipe]) extends IdentityTask

class AggregatePipe
case class AggMatch(`$match`: Match) extends AggregatePipe

case class Match(`$or`: List[OrElem], password: String)
class OrElem
case class UserName(username: String) extends OrElem
case class Phone(phone: String) extends OrElem
case class Email(email: String) extends OrElem
case class PenName(pen_name: String) extends OrElem

case class Limit(`$limit`: Int) extends AggregatePipe
case class Project(`$project`: IdProject) extends AggregatePipe
case class IdProject(_id: Int)
case class ObjectId(`$oid`: String)
case class BsonObjectId(_id: ObjectId)

case class AccountVerifyByAggregateResult(taskId: String, result:Option[BsonObjectId]) extends IdentityTask
