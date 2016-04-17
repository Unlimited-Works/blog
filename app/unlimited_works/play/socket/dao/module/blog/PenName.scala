package unlimited_works.play.socket.dao.module.blog

import lorance.rxscoket.presentation.json.IdentityTask
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
    *
    * @param accountId
    */
  def get(accountId: String) = {
    val d = DaoCommunicate.modelSocket.flatMap{s =>
      val rsp = s.sendWithResult[PenNameRsp, PenNameReq](PenNameReq(accountId), Some(x => x.first))
      toFuture(rsp)
    }
    d
  }

  private def toFuture(observable: Observable[PenNameRsp]): Future[PenNameRsp] = {
    val p = Promise[PenNameRsp]
    observable.subscribe(
      s => p.trySuccess(s),
      e => p.tryFailure(e)
    )

    p.future
  }

  case class PenNameReq(accountId: String, taskId: String = "pen_name") extends IdentityTask
  case class PenNameRsp(pen_name: String, taskId: String) extends IdentityTask
}
