package unlimited_works.play.socket.dao.module

import lorance.rxscoket.presentation.json.IdentityTask
import rx.lang.scala.Observable
import unlimited_works.play.socket.DaoCommunicate
import unlimited_works.play.socket.dao.Method

import lorance.rxscoket.presentation.getTaskId

import scala.concurrent.Promise
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * {
  * "taskId": "thread-1:timestamp",//if not have represent needn't call back
    *"dataBase": "helloworld",
    *"collection": "accounts",
    *"method": "count",
    *"params": {
      *{"$or": [
        *{"username": "administrator"},
        *{"phone":"110"},
        *{"email":"123@xxx.com"},
        *{"pen_name":"lorancechen"}
      *]},
      *{"password": "12345_md5"}
    *}
  *}
  */
object LoginModule {
  val dBName = "helloworld"
  val collName = "account"
  def verify(account: String, password: String) = {
    val proto = AccountVerify(getTaskId, dBName, collName, Method.COUNT,
      Params(Match(List(UserName(account), Phone(account), Email(account), PenName(account)), password)) )
    val p = Promise[AccountVerifyResult]
    DaoCommunicate.getDefaultInstance.flatMap(_.sendWithResult[AccountVerifyResult, AccountVerify](proto))
//    DaoCommunicate.getDefaultInstance.sendWithResult[AccountVerifyResult, AccountVerify](proto)
  }

  /**
    * find
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
    DaoCommunicate.getDefaultInstance.flatMap{o => o.sendWithResult[AccountVerifyByAggregateResult, AccountVerifyByAggregate](proto)}
//    DaoCommunicate.getDefaultInstance.sendWithResult[AccountVerifyByAggregateResult, AccountVerifyByAggregate](proto)
  }
}

case class AccountVerify(taskId: String, dataBase: String, collection: String, method: String, params: Params) extends IdentityTask

case class Params(`match`: Match)
case class Match(`$or`: List[OrElem], password: String)
class OrElem
case class UserName(username: String) extends OrElem
case class Phone(phone: String) extends OrElem
case class Email(email: String) extends OrElem
case class PenName(pen_name: String) extends OrElem

case class AccountVerifyResult(taskId: String, count: Int) extends IdentityTask

/**
  * find a account by aggregate limit return _id
  */
case class AccountVerifyByAggregate(taskId: String, dataBase: String, collection: String, method: String, params: List[AggregatePipe]) extends IdentityTask

class AggregatePipe
case class AggMatch(`$match`: Match) extends AggregatePipe
case class Limit(`$limit`: Int) extends AggregatePipe
case class Project(`$project`: IdProject) extends AggregatePipe
case class IdProject(_id: Int)
case class ObjectId(`$oid`: String)

case class AccountVerifyByAggregateResult(taskId: String, _id: ObjectId) extends IdentityTask

