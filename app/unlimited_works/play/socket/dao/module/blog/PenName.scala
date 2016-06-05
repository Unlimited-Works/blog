package unlimited_works.play.socket.dao.module.blog

import lorance.rxscoket.presentation.json.IdentityTask
import unlimited_works.play.playLogger
import rx.lang.scala.Observable
import unlimited_works.play.socket.DaoCommunicate

import scala.concurrent.{Promise, Future}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  *
  */
object PenName {
  /**
    * {
    *   taskId: "pen_name"
    *   accountId:
    * }
    * todo check - should handle errors if penName not exist
    * @param accountId
    */
  def get(accountId: String) = {
    val d = DaoCommunicate.modelSocket.flatMap{s =>
      val rsp = s.sendWithResult[PenNameRsp, PenNameReq](PenNameReq(accountId,  "pen_name"), Some(x => x.first))
      toFuture(rsp)
    }
    d
  }

  private def toFuture(observable: Observable[PenNameRsp]): Future[PenNameRsp] = {
    val p = Promise[PenNameRsp]
    observable.subscribe(
      s => {
        playLogger.log(s"pen name $s", -100)
        p.trySuccess(s)},
      e => p.tryFailure(e)
    )

    p.future
  }

  case class PenNameReq(accountId: String, model: String, taskId: String = lorance.rxscoket.presentation.getTaskId) extends IdentityTask

  //todo pen_name to Option make we know not match rather then wait until timeout
  case class PenNameRsp(pen_name: String, taskId: String) extends IdentityTask
}
